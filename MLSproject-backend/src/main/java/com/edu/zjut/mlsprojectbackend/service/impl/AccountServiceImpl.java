package com.edu.zjut.mlsprojectbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.zjut.mlsprojectbackend.entity.EmailVerificationMessage;
import com.edu.zjut.mlsprojectbackend.entity.dto.Account;
import com.edu.zjut.mlsprojectbackend.entity.vo.request.ConfirmResetVO;
import com.edu.zjut.mlsprojectbackend.entity.vo.request.EmailRegisterVO;
import com.edu.zjut.mlsprojectbackend.entity.vo.request.EmailResetVO;
import com.edu.zjut.mlsprojectbackend.mapper.AccountMapper;
import com.edu.zjut.mlsprojectbackend.service.AccountService;
import com.edu.zjut.mlsprojectbackend.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 账户信息处理相关服务
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

	@Resource
	StringRedisTemplate stringRedisTemplate;
	@Resource
	RabbitTemplate rabbitTemplate;
	@Resource
	PasswordEncoder encoder;
	/**
	 * 从数据库中通过用户名或邮箱查找用户详细信息
	 * @param username 用户名
	 * @return 用户详细信息
	 * @throws UsernameNotFoundException 如果用户未找到则抛出此异常
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = this.findAccountByName(username);
		if (account == null)
			throw new UsernameNotFoundException("用户名或密码错误");
		return User
				.withUsername(username)
				.password(account.getPassword())
				.roles(account.getRole())
				.build();
	}

	public Account findAccountByName(String text) {
		return this.query()
				.eq("username", text).or()
				.eq("email", text)
				.one();
	}

	@Override
	public boolean existsByEmail(String email) {
		return this.baseMapper.exists(Wrappers.<Account>query().eq("email", email));
	}
	@Override
	public boolean existsByUsername(String username) {
		return this.baseMapper.exists(Wrappers.<Account>query().eq("username", username));
	}
	/**
	 * 生成注册验证码存入Redis中，并将邮件发送请求提交到消息队列等待发送
	 * @param type 类型
	 * @param email 邮件地址
	 * @param address 请求IP地址
	 * @return 操作结果，null表示正常，否则为错误原因
	 */
	public String registerEmailVerifyCode(String type, String email, String address) {
		synchronized (address.intern()) {
			Random random = new Random();
			int code = random.nextInt(899999) + 100000;
			EmailVerificationMessage message = new EmailVerificationMessage(type, email, code);
			rabbitTemplate.convertAndSend(Const.MQ_MAIL, message);
			stringRedisTemplate.opsForValue()
					.set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
			return null;
		}
	}
	/**
	 * 邮件验证码注册账号操作，需要检查验证码是否正确以及邮箱、用户名是否存在重名
	 * @param vo 注册基本信息
	 * @return 操作结果，null表示正常，否则为错误原因
	 */
	@Override
	public String registerEmailAccount(EmailRegisterVO vo) {
		String email = vo.getEmail();
		String username = vo.getUsername();
		String code = this.getEmailVerifyCode(email);
		if (code == null) return "请先获取验证码";
		if (!(code.equals(vo.getCode()))) return "验证码错误";
		if (this.existsByEmail(email)) return "此邮箱已被注册";
		if (this.existsByUsername(username)) return "此用户名已被注册";
		String password = encoder.encode(vo.getPassword());
		Account account = new Account(null, username, password, email, "user", new Date(), null, null);
		if (this.save(account)) {
			stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
			return null;
		} else {
			return "内部错误，请联系管理员";
		}
	}
	/**
	 * 重置密码确认操作，验证验证码是否正确
	 * @param vo 验证基本信息
	 * @return 操作结果，null表示正常，否则为错误原因
	 */
	@Override
	public String resetConfirm(ConfirmResetVO vo) {
		String email = vo.getEmail();
		String code = this.getEmailVerifyCode(email);
		if (code == null) return "请先获取验证码";
		if (!(code.equals(vo.getCode()))) return "验证码错误，请重新输入";
		return null;
	}
	/**
	 * 邮件验证码重置密码操作，需要检查验证码是否正确
	 * @param vo 重置基本信息
	 * @return 操作结果，null表示正常，否则为错误原因
	 */
	@Override
	public String resetEmailAccountPassword(EmailResetVO vo) {
		String email = vo.getEmail();
		String verify = this.resetConfirm(new ConfirmResetVO(email, vo.getCode()));
		if (verify != null) return verify;
		String password = encoder.encode(vo.getPassword());
		boolean update = this.update().eq("email", email).set("password", password).update();
		if (update) {
			stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
		}
		return null;
	}
	/**
	 * 根据用户名获取所有用户信息
	 * @param name 用户名
	 * @return 操作结果，null表示正常，否则为错误原因
	 */
	@Override
	public List<Account> getAllAccountByName(String name) {
		QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("username", name);
		return this.baseMapper.selectList(queryWrapper);
	}
	/**
	 * 根据用户id注销用户信息
	 * @param id 用户名
	 * @return 操作结果，null表示正常，否则为错误原因
	 */
	@Override
	public String deleteAccount(Integer id) {
		if(this.removeById(id)) {
			return null;
		}else{
			return "删除失败";
		}
	}
	/**
	 * 更新用户权限
	 * @param id 用户id
	 * @param role 用户权限
	 * @return 操作结果，null表示正常，否则为错误原因
	 */
	@Override
	public String updateUserRole(Integer id, String role) {
		boolean update = this.update()
					.eq("id", id)
					.set("role", role)
					.update();
		if(update) return null;
		else return "更新职位失败";
	}

	/**
	 * 获取Redis中存储的邮件验证码
	 * @param email 电邮
	 * @return 验证码
	 */
	private String getEmailVerifyCode(String email) {
		String key = Const.VERIFY_EMAIL_DATA + email;
		return stringRedisTemplate.opsForValue().get(key);
	}

}

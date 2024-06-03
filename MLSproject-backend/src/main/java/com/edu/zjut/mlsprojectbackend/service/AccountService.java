package com.edu.zjut.mlsprojectbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.zjut.mlsprojectbackend.entity.dto.Account;
import com.edu.zjut.mlsprojectbackend.entity.vo.request.ConfirmResetVO;
import com.edu.zjut.mlsprojectbackend.entity.vo.request.EmailRegisterVO;
import com.edu.zjut.mlsprojectbackend.entity.vo.request.EmailResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AccountService extends IService<Account>, UserDetailsService {
	Account findAccountByName(String text);

	String registerEmailVerifyCode(String type, String email, String ip);

	String registerEmailAccount(EmailRegisterVO vo);

	String resetConfirm(ConfirmResetVO vo);

	String resetEmailAccountPassword(EmailResetVO vo);
	List<Account> getAllAccountByName(String name);
	String deleteAccount(Integer id);
	String updateUserRole(Integer id, String role);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
}

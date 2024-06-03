package com.edu.zjut.mlsprojectbackend.controller;


import com.edu.zjut.mlsprojectbackend.entity.RestBean;
import com.edu.zjut.mlsprojectbackend.entity.vo.request.ConfirmResetVO;
import com.edu.zjut.mlsprojectbackend.entity.vo.request.EmailRegisterVO;
import com.edu.zjut.mlsprojectbackend.entity.vo.request.EmailResetVO;
import com.edu.zjut.mlsprojectbackend.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

/**
 * 用于验证相关Controller包含用户的注册、重置密码等操作
 */
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

	@Resource
	AccountService service;

	/**
	 * 请求邮件验证码
	 * @param email 请求邮件
	 * @param type 类型
	 * @param request 请求
	 * @return 是否请求成功
	 */
	@GetMapping("/ask-code")
	public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
	                                    @RequestParam @Pattern(regexp = "(register|reset)") String type,
	                                    HttpServletRequest request) {
		return this.messageHandle(() ->
				service.registerEmailVerifyCode(type, email, request.getRemoteAddr()));
	}

	/**
	 * 进行用户注册操作，需要先请求邮件验证码
	 * @param vo 注册信息
	 * @return 是否注册成功
	 */
	@PostMapping("/register")
	public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo) {
		return this.messageHandle(() ->
				service.registerEmailAccount(vo));
	}

	/**
	 * 执行密码重置确认，检查验证码是否正确
	 * @param vo 密码重置信息
	 * @return 是否操作成功
	 */
	@PostMapping("/reset-confirm")
	public RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO vo) {
		return this.messageHandle(() -> service.resetConfirm(vo));
	}

	/**
	 * 执行密码重置操作
	 * @param vo 密码重置信息
	 * @return 是否操作成功
	 */
	@PostMapping("/reset-password")
	public RestBean<Void> resetConfirm(@RequestBody @Valid EmailResetVO vo) {
		return this.messageHandle(() -> service.resetEmailAccountPassword(vo));
	}
	/**
	 * 针对于返回值为String作为错误信息的方法进行统一处理
	 * @param action 具体操作
	 * @return 响应结果
	 */
	private RestBean<Void> messageHandle(Supplier<String> action) {
		String message = action.get();
		return message == null ? RestBean.success() : RestBean.failure(400, message);
	}
}

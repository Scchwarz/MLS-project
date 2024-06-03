package com.edu.zjut.mlsprojectbackend.config;

import com.edu.zjut.mlsprojectbackend.entity.RestBean;
import com.edu.zjut.mlsprojectbackend.entity.dto.Account;
import com.edu.zjut.mlsprojectbackend.entity.vo.response.AuthorizeVO;
import com.edu.zjut.mlsprojectbackend.filter.JwtAuthorizeFilter;
import com.edu.zjut.mlsprojectbackend.service.AccountService;
import com.edu.zjut.mlsprojectbackend.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import java.io.IOException;
import java.io.PrintWriter;
/**
 * SpringSecurity相关配置
 */
@Configuration
public class SecurityConfiguration {
	@Resource
	JwtUtils utils;

	@Resource
	JwtAuthorizeFilter jwtAuthorizeFilter;

	@Resource
	AccountService accountService;

	/**
	 * 针对于 SpringSecurity 6 的新版配置方法
	 * @param http 配置器
	 * @return 自动构建的内置过滤器链
	 * @throws Exception 可能的异常
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(conf -> conf
						.requestMatchers("/api/**", "/error").permitAll()
						.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(conf -> conf
						.loginProcessingUrl("/api/auth/login")
						.successHandler(this::onAuthenticationSuccess)
						.failureHandler(this::onAuthenticationFailure)
				)
				.logout(conf -> conf
						.logoutUrl("/api/auth/logout")
						.logoutSuccessHandler(this::onLogoutSuccess)
				)
				.exceptionHandling(conf -> conf
						.authenticationEntryPoint(this::onUnauthorized)
						.accessDeniedHandler(this::onAccessDeny)
				)
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(conf -> conf
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	/**
	 * 包含：
	 * - 登录成功
	 * - 登录失败
	 * - 未登录拦截/无权限拦截
	 * @param request 请求
	 * @param response 响应
	 * @param exception 异常或是验证实体
	 * @throws IOException 可能的异常
	 */
	public void onUnauthorized(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException{
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
	}

	public void onAccessDeny(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException{
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(RestBean.forbidden(exception.getMessage()).asJsonString());
	}

	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		User user = (User) authentication.getPrincipal();
		Account account = accountService.findAccountByName(user.getUsername());
		String token = utils.createJwt(user, account.getId(), account.getUsername());
		AuthorizeVO vo = new AuthorizeVO();
		BeanUtils.copyProperties(account, vo);
		vo.setExpire(utils.expireTime());
		vo.setToken(token);
		response.getWriter().write(RestBean.success(vo).asJsonString());
	}

	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException{
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(RestBean.failure(401, exception.getMessage()).asJsonString());
	}
	/**
	 * 退出登录处理，将对应的Jwt令牌列入黑名单不再使用
	 * @param request 请求
	 * @param response 响应
	 * @param authentication 验证实体
	 * @throws IOException 可能的异常
	 */
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		PrintWriter writer = response.getWriter();
		String authorization = request.getHeader("Authorization");
		if (utils.invalidateJwt(authorization)) {
			writer.write(RestBean.success().asJsonString());
		} else {
			writer.write(RestBean.failure(400, "退出失败").asJsonString());
		}
	}
}

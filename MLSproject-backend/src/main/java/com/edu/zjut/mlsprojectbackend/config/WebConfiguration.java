package com.edu.zjut.mlsprojectbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 一般Web服务相关配置
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	/**
	*设置虚拟路径
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//文件磁盘图片url 映射
		//配置server虚拟路径，handler为前台访问的目录，locations为files相对应的本地路径
		registry.addResourceHandler("/api/avatar/**")
				.addResourceLocations("file:E:\\软件工程\\课设\\mls-project\\MLSproject-backend\\mlsproject-frontend\\src\\assets\\avatar\\");
		registry.addResourceHandler("/api/files/**")
				.addResourceLocations("file:E:\\软件工程\\课设\\mls-project\\MLSproject-backend\\mlsproject-frontend\\src\\assets\\pdfFiles\\");
	}
}

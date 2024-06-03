package com.edu.zjut.mlsprojectbackend.config;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ消息队列配置
 */
@Configuration
public class RabbitConfiguration {

	@Bean("mailQueue")
	public Queue queue() {
		return QueueBuilder
				.durable("mail")
				.build();
	}

	@Bean("jacksonConverter")
	public Jackson2JsonMessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}

}

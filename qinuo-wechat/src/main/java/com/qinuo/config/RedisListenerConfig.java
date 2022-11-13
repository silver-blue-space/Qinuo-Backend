package com.qinuo.config;

import cn.hutool.core.util.StrUtil;
import com.qinuo.listener.RedisKeyExpirationListener;
import com.qinuo.service.IQnOrderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;


@Configuration
@AllArgsConstructor
public class RedisListenerConfig {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private RedisConfigProperties redisConfigProperties;

	@Autowired
	private IQnOrderService iQnOrderService;

	@Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(new RedisKeyExpirationListener(redisTemplate, redisConfigProperties, iQnOrderService),
				new PatternTopic(StrUtil.format("__keyevent@{}__:expired", redisConfigProperties.getDatabase())));
		return container;
	}
}


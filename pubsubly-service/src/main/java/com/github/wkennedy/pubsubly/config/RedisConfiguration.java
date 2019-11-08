package com.github.wkennedy.pubsubly.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.inbound.RedisInboundChannelAdapter;
import org.springframework.messaging.MessageChannel;

@Configuration
public class RedisConfiguration {

    @Value("${redis.topic.pattern}")
    private String redisTopicPattern;

    @Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisInboundChannelAdapter redisInboundChannelAdapter(RedisConnectionFactory redisConnectionFactory, @Autowired MessageChannel inboundMessageChannel) {
        RedisInboundChannelAdapter redisInboundChannelAdapter = new RedisInboundChannelAdapter(redisConnectionFactory);
        redisInboundChannelAdapter.setTopicPatterns(redisTopicPattern);
        redisInboundChannelAdapter.setOutputChannel(inboundMessageChannel);

        return redisInboundChannelAdapter;
    }

    @Bean
    public RedisSerializer redisSerializer() {
        return new StringRedisSerializer();
    }

}
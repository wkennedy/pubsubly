package com.github.wkennedy.pubsubly.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.inbound.RedisInboundChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty("pubsubly.redis.enabled")
public class RedisConfiguration {

    @Value("${redis.topic.pattern:}")
    private String redisTopicPattern;

    @Value("${redis.topic.names:@null}")
    private String[] redisTopicNames;

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisInboundChannelAdapter redisInboundChannelAdapter(RedisConnectionFactory redisConnectionFactory, @Autowired MessageChannel inboundMessageChannel) {
        RedisInboundChannelAdapter redisInboundChannelAdapter = new RedisInboundChannelAdapter(redisConnectionFactory);
        if(StringUtils.isEmpty(redisTopicPattern)) {
            redisInboundChannelAdapter.setTopics(redisTopicNames);
        } else {
            redisInboundChannelAdapter.setTopicPatterns(redisTopicPattern);
        }
        redisInboundChannelAdapter.setOutputChannel(inboundMessageChannel);

        return redisInboundChannelAdapter;
    }

    @Bean
    public RedisSerializer redisSerializer() {
        return new StringRedisSerializer();
    }

}
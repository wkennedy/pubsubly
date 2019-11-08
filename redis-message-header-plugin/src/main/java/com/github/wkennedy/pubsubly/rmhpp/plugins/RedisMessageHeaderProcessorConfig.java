package com.github.wkennedy.pubsubly.rmhpp.plugins;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class RedisMessageHeaderProcessorConfig {

    @Bean("redisMessageHeaderProcessorPlugin")
    @ConditionalOnMissingBean
    RedisMessageHeaderProcessorPlugin redisMessageHeaderProcessorPlugin(){
        return new RedisMessageHeaderProcessorPlugin();
    }
}

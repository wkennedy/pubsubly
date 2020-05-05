package com.github.wkennedy.pubsubly.demo;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.listener.ChannelTopic;

@Profile("demo")
@Configuration
public class DemoConfig {

    private static final String KAFKA_BID_TOPIC = "DEMO-KAFKA_BID_TOPIC";
    private static final String KAFKA_ORDER_TOPIC = "DEMO-KAFKA_ORDER_TOPIC";
    private static final String KAFKA_USER_TOPIC = "DEMO-KAFKA_USER_TOPIC";
    private static final String REDIS_TOPIC = "REDIS-USER-SESSION";
    private static final String EXTERNAL_TOPIC = "DEMO-EXTERNAL_TOPIC";

    @Bean
    public NewTopic topic1() {
        return new NewTopic(KAFKA_BID_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic topic2() {
        return new NewTopic(KAFKA_ORDER_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic topic3() {
        return new NewTopic(KAFKA_USER_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic topic4() {
        return new NewTopic(EXTERNAL_TOPIC, 1, (short) 1);
    }

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic(REDIS_TOPIC);
    }

}

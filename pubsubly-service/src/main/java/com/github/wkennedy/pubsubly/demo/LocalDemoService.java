package com.github.wkennedy.pubsubly.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wkennedy.pubsubly.config.KafkaProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Profile("local-demo")
@Component
@EnableScheduling
public class LocalDemoService {
    private static final String EVENT_ID = "eventId";
    private static final String EVENT_NAME = "eventName";
    private static final String CORRELATION_ID = "correlationId";

    private static final String KAFKA_BID_TOPIC = "DEMO-KAFKA_BID_TOPIC";
    private static final String KAFKA_ORDER_TOPIC = "DEMO-KAFKA_ORDER_TOPIC";
    private static final String KAFKA_USER_TOPIC = "DEMO-KAFKA_USER_TOPIC";
    private static final String EXTERNAL_TOPIC = "EXTERNAL_TOPIC";


    private KafkaProperties kafkaProperties;

    private KafkaTemplate<?, ?> kafkaTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Random randomGenerator = new Random();

    public LocalDemoService(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
        this.kafkaTemplate = kafkaTemplate();
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        return kafkaTemplate;
    }

    @Scheduled(fixedRate = 60000)
    public void runExternalTopicPublisher() {
        Map<String, Object> headers = new HashMap<>();
        String eventId = UUID.randomUUID().toString();
        headers.put(EVENT_ID, eventId);
        headers.put(EVENT_NAME, "BIG_ERROR");
        Message<String> message = MessageBuilder.createMessage("This is a demo message with an error", new MessageHeaders(headers));
        kafkaTemplate.setDefaultTopic(EXTERNAL_TOPIC);
        kafkaTemplate.send(message);
    }
}

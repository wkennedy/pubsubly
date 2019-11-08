package com.github.wkennedy.pubsubly.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageChannel;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    private final KafkaProperties kafkaProperties;

    @Value("${kafka.topic.pattern:@null}")
    private String topicPattern;

    @Value("${kafka.topic.names:@null}")
    private String[] topicNames;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Primary
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(@Autowired RetryTemplate retryTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setRetryTemplate(retryTemplate);
        return factory;
    }

    @Bean
    public RetryTemplate retryTemplate(@Autowired RetryPolicy retryPolicy) {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean
    public RetryPolicy retryPolicy() {
        final SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(5);

        final Map<Class<? extends Throwable>, RetryPolicy> policyMap = new HashMap<>();
        policyMap.put(SerializationException.class, new NeverRetryPolicy());
        policyMap.put(IOException.class, simpleRetryPolicy);

        final ExceptionClassifierRetryPolicy retryPolicy = new ExceptionClassifierRetryPolicy();
        retryPolicy.setPolicyMap(policyMap);

        return retryPolicy;
    }

    @Bean
    public RecoveryCallback<Void> recoveryCallback() {
        return (RetryContext arg0) -> {
            ConsumerRecord<?, ?> record = (ConsumerRecord<?, ?>) arg0.getAttribute("record");
            log.info("Recovery callback: " + record);
            Acknowledgment acknowledgment = (Acknowledgment) arg0.getAttribute("acknowledgment");
            acknowledgment.acknowledge();
            return null;
        };
    }

    @Bean
    public KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter(@Autowired MessageChannel inboundMessageChannel) {
        KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter =
                new KafkaMessageDrivenChannelAdapter<>(kafkaListener());

        kafkaMessageDrivenChannelAdapter.setOutputChannel(inboundMessageChannel);
        return kafkaMessageDrivenChannelAdapter;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> kafkaListener() {
        ContainerProperties containerProperties;
        if(StringUtils.isEmpty(topicPattern)) {
            containerProperties = new ContainerProperties(topicNames);
        } else {
            containerProperties = new ContainerProperties(Pattern.compile(topicPattern));
        }
        ConcurrentMessageListenerContainer<String, String> stringStringConcurrentMessageListenerContainer = new ConcurrentMessageListenerContainer<>(consumerFactory(), containerProperties);
        stringStringConcurrentMessageListenerContainer.setConcurrency(1);
        return stringStringConcurrentMessageListenerContainer;
    }
}

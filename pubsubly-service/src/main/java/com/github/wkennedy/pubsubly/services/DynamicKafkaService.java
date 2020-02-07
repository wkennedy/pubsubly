package com.github.wkennedy.pubsubly.services;

import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class DynamicKafkaService {

    private final ConcurrentMessageListenerContainer<String, String> kafkaListener;

    private final ConsumerFactory<String, String> consumerFactory;

    private final MessageChannel inboundMessageChannel;

    private final Map<String, KafkaMessageDrivenChannelAdapter<String, String>> dynamicKafkaAdapters;

    public DynamicKafkaService(ConcurrentMessageListenerContainer<String, String> kafkaListener, ConsumerFactory<String, String> consumerFactory, MessageChannel inboundMessageChannel, Map<String, KafkaMessageDrivenChannelAdapter<String, String>> dynamicKafkaAdapters) {
        this.kafkaListener = kafkaListener;
        this.consumerFactory = consumerFactory;
        this.inboundMessageChannel = inboundMessageChannel;
        this.dynamicKafkaAdapters = dynamicKafkaAdapters;
    }

    public void stopDefaultListener() {
        kafkaListener.stop();
    }

    public void startDefaultListener() {
        kafkaListener.start();
    }

    public void startPattern(String topicPattern) {
        ContainerProperties containerProperties = new ContainerProperties(Pattern.compile(topicPattern));
        createAndAddAdapter(topicPattern, containerProperties);
    }

    public void stopPattern(String topicPattern) {
        KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter = dynamicKafkaAdapters.get(topicPattern);
        if (kafkaMessageDrivenChannelAdapter != null) {
            kafkaMessageDrivenChannelAdapter.stop();
        }
        dynamicKafkaAdapters.remove(topicPattern);
    }

    public void startTopics(List<String> topicNames) {
        for (String topicName : topicNames) {
            ContainerProperties containerProperties;
            containerProperties = new ContainerProperties(topicName);

            createAndAddAdapter(topicName, containerProperties);
        }
    }

    public void stopTopics(List<String> topicNames) {
        for (String topicName : topicNames) {
            KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter = dynamicKafkaAdapters.get(topicName);
            if (kafkaMessageDrivenChannelAdapter != null) {
                kafkaMessageDrivenChannelAdapter.stop();
            }
            dynamicKafkaAdapters.remove(topicName);
        }
    }

    private void createAndAddAdapter(String key, ContainerProperties containerProperties) {
        KafkaMessageListenerContainer<String, String> kafkaMessageListenerContainer = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        KafkaMessageDrivenChannelAdapter<String, String> messageDrivenChannelAdapter = kafkaMessageDrivenChannelAdapter(kafkaMessageListenerContainer);
        messageDrivenChannelAdapter.afterPropertiesSet();
        messageDrivenChannelAdapter.setMessageConverter(new MessagingMessageConverter());
        dynamicKafkaAdapters.put(key, messageDrivenChannelAdapter);
        messageDrivenChannelAdapter.start();
    }

    public KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter(AbstractMessageListenerContainer<String, String> kafkaListener) {
        KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter =
                new KafkaMessageDrivenChannelAdapter<>(kafkaListener);

        kafkaMessageDrivenChannelAdapter.setOutputChannel(inboundMessageChannel);
        return kafkaMessageDrivenChannelAdapter;
    }

    public Set<String> getDynamicTopics() {
        return dynamicKafkaAdapters.keySet();
    }
}

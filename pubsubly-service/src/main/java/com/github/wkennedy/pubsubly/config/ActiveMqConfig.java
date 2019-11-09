package com.github.wkennedy.pubsubly.config;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.jms.ChannelPublishingJmsMessageListener;
import org.springframework.integration.jms.JmsMessageDrivenEndpoint;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.messaging.MessageChannel;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

@Configuration
@ConditionalOnProperty("activemq.topic.names")
public class ActiveMqConfig {

    @Value("${activemq.topic.names}")
    private String activeMqTopicNames;

    @Bean
    public Destination destination() {
        return new ActiveMQTopic(activeMqTopicNames);
    }

    @Bean
    public JmsMessageDrivenEndpoint jmsMessageDrivenEndpoint(@Autowired AbstractMessageListenerContainer messageListenerContainer, @Autowired MessageChannel inboundMessageChannel) {
        JmsMessageDrivenEndpoint endpoint = new JmsMessageDrivenEndpoint(
                messageListenerContainer,
                channelPublishingJmsMessageListener());
        endpoint.setOutputChannel(inboundMessageChannel);

        return endpoint;
    }

    @Bean
    public AbstractMessageListenerContainer simpleMessageListenerContainer(@Autowired ConnectionFactory connectionFactory, @Autowired Destination destination) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setDestination(destination);
        container.setPubSubDomain(true);
        return container;
    }

    @Bean
    public ChannelPublishingJmsMessageListener channelPublishingJmsMessageListener() {
        return new ChannelPublishingJmsMessageListener();
    }

}

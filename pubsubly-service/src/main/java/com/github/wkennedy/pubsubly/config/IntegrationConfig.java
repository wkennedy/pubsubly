package com.github.wkennedy.pubsubly.config;

import com.github.wkennedy.pubsubly.listeners.PluginExecutorMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageChannel;

@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannel inboundMessageChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundMessageIntegrationFlow(@Autowired PluginExecutorMessageHandler pluginExecutorHandler, MessageChannel inboundMessageChannel) {
        return IntegrationFlows.from(inboundMessageChannel)
                .handle(pluginExecutorHandler)
                .get();
    }
}

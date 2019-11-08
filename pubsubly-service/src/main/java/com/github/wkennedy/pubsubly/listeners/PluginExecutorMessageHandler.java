package com.github.wkennedy.pubsubly.listeners;

import com.github.wkennedy.pubsubly.processors.PluginExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class PluginExecutorMessageHandler extends AbstractMessageHandler {
    private static final Logger log = LoggerFactory.getLogger(PluginExecutorMessageHandler.class);

    @Value("${persistMessages.enabled:true}")
    private boolean persistMessages;

    private final PluginExecutor pluginExecutor;

    public PluginExecutorMessageHandler(PluginExecutor pluginExecutor) {
        this.pluginExecutor = pluginExecutor;
    }

    @Override
    protected void handleMessageInternal(Message<?> message){
        log.debug("INTERNAL HEADERS: " + message.getHeaders().toString());
        pluginExecutor.execute(message, persistMessages);
    }
}

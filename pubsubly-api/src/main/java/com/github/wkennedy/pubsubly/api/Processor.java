package com.github.wkennedy.pubsubly.api;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.plugin.metadata.PluginMetadata;

public interface Processor extends PluginMetadata {
    String process(Message message, MessageHeaders headers, Tag tag);
}

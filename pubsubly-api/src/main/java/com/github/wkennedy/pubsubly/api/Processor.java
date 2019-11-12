package com.github.wkennedy.pubsubly.api;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.plugin.metadata.PluginMetadata;

/**
 * The processor evaluates a Message and/or headers in order to find a piece of data that matches that of the tag value.
 * An example is the HeaderProcessorPlugin in pubsubly-service. It looks for a key in the header that matches the
 * tag value and if it does, then it returns that header value.
 */
public interface Processor extends PluginMetadata {

    /**
     * @param message Match an identifier in the headers or message
     * @param headers Match an identifier in the headers or message
     * @param tag @{@link Tag} Match an identifier in the message or headers with that of the tag value
     * @return String value of the data that matches the identifier (header, key, variable) in the message or headers
     */
    String process(Message message, MessageHeaders headers, Tag tag);
}

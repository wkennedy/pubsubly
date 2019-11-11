package com.github.wkennedy.pubsubly.plugins;

import com.github.wkennedy.pubsubly.api.Tag;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HeaderProcessorPluginTest {

    private HeaderProcessorPlugin headerProcessorPlugin = new HeaderProcessorPlugin();

    @Test
    public void process() {
        Message<String> message = MessageBuilder.withPayload("test").build();
        Map<String, Object> headers = new HashMap<>();
        headers.put("eventId", "12345");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Tag tag = new Tag();
        tag.setValue("eventId");
        tag.setId("eventId");
        String process = headerProcessorPlugin.process(message, messageHeaders, tag);
        assertEquals("12345", process);
    }
}
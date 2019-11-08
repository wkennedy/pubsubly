package com.github.wkennedy.pubsubly.rmhpp.plugins;

import com.github.wkennedy.pubsubly.api.Tag;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class RedisMessageHeaderProcessorPluginTest {

    private RedisMessageHeaderProcessorPlugin redisMessageHeaderProcessorPlugin = new RedisMessageHeaderProcessorPlugin();

    @Test
    public void process() {
        Message<String> message = MessageBuilder.withPayload("{\"payload\":\"Person Added\",\"headers\":{\"eventId\":\"8ccc36ab-c499-49ad-b637-56537719c64a\",\"eventName\":\"ADD_PERSON_EVENT\",\"id\":\"5ede4e08-c801-a17f-e880-1b66da61b186\",\"timestamp\":1573188961999}}").build();
        Tag tag = new Tag();
        tag.setId("eventName");
        tag.setValue("eventName");
        redisMessageHeaderProcessorPlugin.process(message, message.getHeaders(), tag);
    }
}
package com.github.wkennedy.pubsubly.listeners;

import com.github.wkennedy.pubsubly.util.RandomDate;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Ignore
@RunWith(SpringRunner.class)
class TopicListenerTest {

    @Autowired
    private PluginExecutorMessageHandler topicListener;

    @Test
    void listen() {
    }

    @Test
    void processPayload() {

        String payload = "{key: 'value'}";
        for(int i = 0; i < 1000; i++) {
            Message<String> message = MessageBuilder.createMessage(payload, getMessageHeaders());
        }
    }

    private MessageHeaders getMessageHeaders() {
        OffsetDateTime offsetDateTime = RandomDate.getDate();
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("eventName", "TEST_EVENT");
        headerMap.put("eventId", UUID_list.get(random.nextInt(20)));
        headerMap.put("kafka_receivedTimestamp", offsetDateTime.toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli());
        headerMap.put("kafka_receivedTopic", "GCC-140419-PUBSUBLY-DEV-EPG-OTA_STATUS_EVENT");

        return new MessageHeaders(headerMap);
    }

    private final Random random = new Random();
    private static List<String> UUID_list = new ArrayList<>();
    static {
        for(int i = 0; i < 20; i++) {
            UUID_list.add(UUID.randomUUID().toString());
        }
    }
}
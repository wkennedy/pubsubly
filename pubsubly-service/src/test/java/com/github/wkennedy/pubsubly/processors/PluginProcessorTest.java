package com.github.wkennedy.pubsubly.processors;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.wkennedy.pubsubly.api.Processor;
import com.github.wkennedy.pubsubly.api.Tag;
import com.github.wkennedy.pubsubly.models.MessageBundle;
import com.github.wkennedy.pubsubly.plugins.HeaderProcessorPlugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PluginProcessorTest {

    @Mock
    private Map<String, Cache<String, MessageBundle>> cacheMap;

    @InjectMocks
    private PluginProcessor pluginProcessor = new PluginProcessor();

    private Cache<String, MessageBundle> cache = Caffeine.newBuilder().build();

    private String uuid = UUID.randomUUID().toString();

    @Before
    public void setup() {
        Processor processor = new HeaderProcessorPlugin();
        pluginProcessor.setProcessor(processor);
        Tag tag = new Tag();
        tag.setValue("eventId");
        tag.setId("eventId");
        pluginProcessor.setTags(Collections.singletonList(tag));

        MessageBundle messageBundle = new MessageBundle();
        Set<String> uuidSet = new HashSet<>();
        uuidSet.add(uuid);
        messageBundle.setMessageUUIDs(uuidSet);
        messageBundle.setTag(tag);
        cache.put("12345", messageBundle);
    }

    @Test
    public void execute() {
        Message<String> message = MessageBuilder.withPayload("test").build();
        Map<String, Object> headers = new HashMap<>();
        headers.put("eventId", "12345");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        when(cacheMap.get("eventId")).thenReturn(cache);

        Map<String, String> execute = pluginProcessor.execute(message, messageHeaders, UUID.randomUUID().toString());
        assertNotNull(execute);
        assertEquals("12345", execute.get("eventId"));
    }
}
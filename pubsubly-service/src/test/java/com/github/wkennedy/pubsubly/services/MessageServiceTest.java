package com.github.wkennedy.pubsubly.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.wkennedy.pubsubly.api.Tag;
import com.github.wkennedy.pubsubly.config.PluginProcessorProperties;
import com.github.wkennedy.pubsubly.models.MessageBundle;
import com.github.wkennedy.pubsubly.models.MessageDetails;
import com.github.wkennedy.pubsubly.models.MessageResource;
import com.github.wkennedy.pubsubly.models.MessageResourceBundle;
import com.google.common.collect.EvictingQueue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceTest {

    private MessageService messageService;
    private String messageUUID = UUID.randomUUID().toString();


    @Before
    public void setup() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("eventId", "12345");
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        Message<String> message = MessageBuilder.createMessage("test", messageHeaders);

        Tag tag = new Tag();
        tag.setValue("eventId");
        tag.setId("eventId");

        Cache<String, MessageResource> messageCache = Caffeine.newBuilder().build();
        Map<String, String> headerKeyMap = new HashMap<>();
        headerKeyMap.put("eventId", "12345");
        MessageResource messageResource = new MessageResource(message, headerKeyMap);
        messageCache.put(messageUUID, messageResource);

        Map<String, Cache<String, MessageBundle>> cacheMap = new HashMap<>();
        MessageBundle messageBundle = new MessageBundle();
        messageBundle.setTag(tag);
        messageBundle.setMessageUUIDs(Collections.singleton(messageUUID));
        Cache<String, MessageBundle> valueCache = Caffeine.newBuilder().build();
        valueCache.put("12345", messageBundle);
        cacheMap.put(tag.getId(), valueCache);

        Queue latestMessageCache = EvictingQueue.create(50);

        PluginProcessorProperties.Processor processor = new PluginProcessorProperties.Processor();
        processor.setId("headerProcessorPlugin");
        processor.setTags(Collections.singletonList(tag));

        PluginProcessorProperties pluginProcessorProperties = new PluginProcessorProperties();
        pluginProcessorProperties.setProcessors(Collections.singletonList(processor));

        ProcessorService processorService = new ProcessorService(pluginProcessorProperties);

        messageService = new MessageService(messageCache, cacheMap, latestMessageCache, processorService);
    }

    @Test
    public void messageResources() {
        MessageResourceBundle messageResourceBundle = messageService.messageResources("eventId", "12345");
        assertNotNull(messageResourceBundle);
        assertEquals("test", messageResourceBundle.getMessageResources().get(0).getMessage().getPayload());
    }

    @Test
    public void messageDetails() {
        MessageDetails messageDetails = messageService.messageDetails("eventId", "12345");
        assertNotNull(messageDetails);
        assertEquals("test", messageDetails.getMessageResourceBundle().getMessageResources().get(0).getMessage().getPayload());
    }

    @Test
    public void searchMessageResourcesByPayload() {
        List<MessageResource> messageResources = messageService.searchMessageResourcesByPayload("test");
        assertFalse(messageResources.isEmpty());
        assertEquals("test", messageResources.get(0).getMessage().getPayload());
    }

    @Test
    public void searchMessageResources() {
        List<MessageResource> messageResources = messageService.searchMessageResources("eventId", "12345");
        assertEquals("test", messageResources.get(0).getMessage().getPayload());
    }

    @Test
    public void getMessageResources() {
        List<MessageResource> messageResources = messageService.getMessageResources(Collections.singleton(messageUUID));
        assertEquals("test", messageResources.get(0).getMessage().getPayload());
    }

    @Test
    public void testGetMessageResources() {
        List<MessageResource> messageResources = messageService.getMessageResources(Collections.singleton(messageUUID), 0, 10);
        assertEquals("test", messageResources.get(0).getMessage().getPayload());
    }

    @Test
    public void streamMessages() {
        //TODO
    }

    @Test
    public void getMessageWithHeaderValues() {
        //TODO
        Map<Map<String, Object>, List<MessageResource>> eventId = messageService.getMessageWithHeaderValues(Collections.singleton("eventId"), null);
    }
}
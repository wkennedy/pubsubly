package com.github.wkennedy.pubsubly.processors;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.wkennedy.pubsubly.api.Processor;
import com.github.wkennedy.pubsubly.api.Tag;
import com.github.wkennedy.pubsubly.models.MessageResource;
import com.github.wkennedy.pubsubly.plugins.HeaderProcessorPlugin;
import com.github.wkennedy.pubsubly.plugins.HeaderValuePriorityProcessorPlugin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static reactor.core.publisher.Mono.when;

@RunWith(MockitoJUnitRunner.class)
public class PluginExecutorTest {

    @InjectMocks
    private PluginExecutor pluginExecutor = new PluginExecutor(new TopicProcessor(Caffeine.newBuilder().build()));

    @Mock
    private Cache<String, MessageResource> messageCache;

    @Mock
    private Queue<MessageResource> latestMessageCache;

    @Mock
    private HeaderValuePriorityProcessorPlugin headerValuePriorityProcessorPlugin;

    @Mock
    private Queue<MessageResource> highPriorityMessageCache;

    @Test
    public void execute() {
        PluginProcessor pluginProcessor = new PluginProcessor();
        Processor processor = new HeaderProcessorPlugin();
        pluginProcessor.setProcessor(processor);
        Tag tag = new Tag();
        tag.setValue("eventId");
        tag.setId("eventId");
        pluginProcessor.setTags(Collections.singletonList(tag));

        pluginExecutor.addPluginProcessor(pluginProcessor);

        Message<String> message = MessageBuilder.withPayload("test").build();
        Map<String, Object> headers = new HashMap<>();
        headers.put("eventId", "12345");
        MessageHeaders messageHeaders = new MessageHeaders(headers);

        doNothing().when(messageCache).put(any(), any());
        doReturn(true).when(latestMessageCache).add(any());
        pluginExecutor.execute(message, false);
    }

    @Test
    public void addPluginProcessor() {
    }
}
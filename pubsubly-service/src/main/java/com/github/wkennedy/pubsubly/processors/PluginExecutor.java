package com.github.wkennedy.pubsubly.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.wkennedy.pubsubly.dao.entities.PubsublyMessagesEntity;
import com.github.wkennedy.pubsubly.dao.repositories.PubsublyMessageRepository;
import com.github.wkennedy.pubsubly.models.MessageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MutableMessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.util.SerializationUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import static com.github.wkennedy.pubsubly.util.HeaderUtil.getTopicName;

public class PluginExecutor {

    private static final Logger log = LoggerFactory.getLogger(PluginExecutor.class);

    private final TopicProcessor topicProcessor;

    @Autowired
    private Cache<String, MessageResource> messageCache;

    private List<PluginProcessor> pluginProcessors = new ArrayList<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PubsublyMessageRepository pubsublyMessageRepository;

    @Autowired
    private Queue<MessageResource> latestMessageCache;

    @Autowired
    public PluginExecutor(TopicProcessor topicProcessor) {
        this.topicProcessor = topicProcessor;
    }

    public void execute(Message<?> message, boolean persistToDatabase) {
        String messageUUID = UUID.randomUUID().toString();
        Map<String, String> headerKeyMap = new HashMap<>();
        for (PluginProcessor pluginProcessor : pluginProcessors) {
            headerKeyMap.putAll(pluginProcessor.execute(message, message.getHeaders(), messageUUID));
        }
        topicProcessor.process(getTopicName(message.getHeaders()), messageUUID);
        Message mutableMessage = MutableMessageBuilder.fromMessage(message).build();
        removeUnwantedHeaders(mutableMessage);
        MessageResource messageResource = new MessageResource(mutableMessage, headerKeyMap);
        messageCache.put(messageUUID, messageResource);
        latestMessageCache.add(messageResource);
        persistMessage(mutableMessage, messageUUID, persistToDatabase);
    }

    private void persistMessage(Message mutableMessage, String messageUUID, boolean persistToDatabase) {
        if(!persistToDatabase) {
            return;
        }

        PubsublyMessagesEntity pubsublyMessagesEntity = new PubsublyMessagesEntity();
        try {
            pubsublyMessagesEntity.setHeaders(objectMapper.writeValueAsString(mutableMessage.getHeaders()).getBytes());
        } catch (JsonProcessingException e) {
            log.error("Error writing headers as bytes for message: " + mutableMessage.getHeaders().toString(), e);
            return;
        }

        pubsublyMessagesEntity.setPayload(SerializationUtils.serialize(mutableMessage.getPayload()));

        pubsublyMessagesEntity.setCreatedDt(new Timestamp(System.currentTimeMillis()));
        pubsublyMessagesEntity.setId(messageUUID);

        pubsublyMessageRepository.save(pubsublyMessagesEntity);
    }

    private void removeUnwantedHeaders( Message message) {
        message.getHeaders().remove("kafka_consumer");
    }

    public void addPluginProcessor(PluginProcessor pluginProcessor) {
        pluginProcessors.add(pluginProcessor);
    }
}

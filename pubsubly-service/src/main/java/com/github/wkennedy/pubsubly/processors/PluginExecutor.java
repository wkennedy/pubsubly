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

/**
 * The PluginExecutor is responsible for executing processors and storing messages in the message cache, with each
 * message having a UUID which is used for lookup in the cache.
 */
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
        //Generate the UUID for the message here. This will be used to look up and track the message
        String messageUUID = UUID.randomUUID().toString();
        Map<String, String> headerKeyMap = new HashMap<>();
        //Loop through the available processors and get the map of tag IDs and values applicable to the message
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

    /**
     * If you want to store messages in a database for redundancy, you can set the property "persistMessages.enabled=true".
     * Keep in mind, you might have lots and lots of messages consumed, so you might not want to store them all. If you do change the scheduler for truncation.
     * @param mutableMessage the consumed message
     * @param messageUUID the unique message ID
     * @param persistToDatabase true if you want to persist to a db, false otherwise.
     */
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

    /**
     * @param message remove unnecessary headers from this
     */
    private void removeUnwantedHeaders(Message message) {
        //We don't want to keep the kafka_consumer around as this can cause circular issues and we don't need it anyway
        message.getHeaders().remove("kafka_consumer");
    }

    public void addPluginProcessor(PluginProcessor pluginProcessor) {
        pluginProcessors.add(pluginProcessor);
    }
}

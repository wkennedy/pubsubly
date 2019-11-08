package com.github.wkennedy.pubsubly.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wkennedy.pubsubly.dao.entities.PubsublyMessagesEntity;
import com.github.wkennedy.pubsubly.dao.repositories.PubsublyMessageRepository;
import com.github.wkennedy.pubsubly.processors.PluginExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageLoader {

    private static final Logger log = LoggerFactory.getLogger(MessageLoader.class);

    private final PubsublyMessageRepository pubsublyMessageRepository;
    private final PluginExecutor pluginExecutor;

    private ObjectMapper objectMapper = new ObjectMapper();

    public MessageLoader(PluginExecutor pluginExecutor, PubsublyMessageRepository pubsublyMessageRepository) {
        this.pluginExecutor = pluginExecutor;
        this.pubsublyMessageRepository = pubsublyMessageRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadMessages() {
        List<PubsublyMessagesEntity> messagesEntities = pubsublyMessageRepository.findAll();
        TypeReference<LinkedHashMap<String,Object>> typeRef = new TypeReference<LinkedHashMap<String,Object>>() {};

        for (PubsublyMessagesEntity messagesEntity : messagesEntities) {
            Object payload = SerializationUtils.deserialize(messagesEntity.getPayload());
            Map<String, Object> headers = null;
            try {
                headers = objectMapper.readValue(messagesEntity.getHeaders(), typeRef);
            } catch (IOException e) {
                log.error("Error trying to map headers: " + new String(messagesEntity.getHeaders()));
            }
            assert payload != null;
            Message<?> message = MessageBuilder.withPayload(payload).copyHeaders(headers).build();
            pluginExecutor.execute(message, false);
        }
    }

    //TODO make configurable via properties
//    @Scheduled(cron = "0 0 * * SAT")
//    public void purgeMessages() {
//        pubsublyMessageRepository.deleteAll();
//    }
}

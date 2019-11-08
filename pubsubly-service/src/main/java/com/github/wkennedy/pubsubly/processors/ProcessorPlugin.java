package com.github.wkennedy.pubsubly.processors;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.wkennedy.pubsubly.api.Processor;
import com.github.wkennedy.pubsubly.api.Tag;
import com.github.wkennedy.pubsubly.models.MessageBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProcessorPlugin {
    private static final Logger log = LoggerFactory.getLogger(ProcessorPlugin.class);

    private Processor processor;
    private List<Tag> tags;

    @Qualifier("cacheMap")
    @Autowired
    private Map<String, Cache<String, MessageBundle>> cacheMap;

    public ProcessorPlugin() {
    }

    public Map<String, String> execute(Message message, MessageHeaders headers, String messageUUID) {
        Map<String, String> headerKeyMap = new HashMap<>();
        for (Tag tag : tags) {
            String value = processor.process(message, headers, tag);
            if(value != null) {
                headerKeyMap.put(tag.getId(), value);
                Cache<String, MessageBundle> cache = cacheMap.get(tag.getId());
                MessageBundle messageBundle = cache.getIfPresent(Objects.requireNonNull(value));
                if (messageBundle != null) {
                    log.trace("Adding message UUID to existing bundle for UUID: "+ messageUUID + " and tag: " + tag.toString());
                    messageBundle.addMessageUUID(messageUUID);
                } else {
                    log.trace("Creating new message bundle for message UUID: "+ messageUUID + " and tag: " + tag.toString());
                    messageBundle = new MessageBundle();
                    messageBundle.setTag(tag);
                    messageBundle.addMessageUUID(messageUUID);
                    cache.put(value, messageBundle);
                }
            }
        }
        return headerKeyMap;
    }

    public Processor getProcessor() {
        return processor;
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}

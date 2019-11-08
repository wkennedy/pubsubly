package com.github.wkennedy.pubsubly.processors;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TopicProcessor {

    private final Cache<String, List<String>> topicCache;

    @Autowired
    public TopicProcessor(Cache<String, List<String>> topicCache) {
        this.topicCache = topicCache;
    }

    void process(String topic, String messageUUID) {
        List<String> topicEvents = topicCache.getIfPresent(topic);
        if (topicEvents == null) {
            topicEvents = new ArrayList<>();
        }
        topicEvents.add(messageUUID);
        topicCache.put(topic, topicEvents);
    }
}

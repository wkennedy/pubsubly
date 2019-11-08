package com.github.wkennedy.pubsubly.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.wkennedy.pubsubly.api.Tag;
import com.github.wkennedy.pubsubly.models.MessageBundle;
import com.github.wkennedy.pubsubly.models.MessageResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static com.google.common.collect.EvictingQueue.create;

@Configuration
public class CacheConfig {

    private final PluginProcessorProperties pluginProcessorProperties;

    public CacheConfig(PluginProcessorProperties pluginProcessorProperties) {
        this.pluginProcessorProperties = pluginProcessorProperties;
    }

    @Bean("cacheMap")
    public Map<String, Cache<String, MessageBundle>> cacheMap() {
        Map<String, Cache<String, MessageBundle>> cacheMap = new HashMap<>();
        List<PluginProcessorProperties.Processor> processors = pluginProcessorProperties.getProcessors();
        List<Tag> globalTags = pluginProcessorProperties.getTags();
        for (Tag globalTag : globalTags) {
            if(cacheMap.containsKey(globalTag.getId())) {
                throw new InvalidConfigurationPropertyValueException("processors.tags.id", globalTag.getId(), "The tag ID exists in more than one tag ID, please update the tag ID: " + globalTag.getId());
            }
            cacheMap.put(globalTag.getId(), Caffeine.newBuilder().build());
        }

        for (PluginProcessorProperties.Processor processor : processors) {
            List<Tag> tags = processor.getTags();
            for (Tag tag : tags) {
                if(cacheMap.containsKey(tag.getId())) {
                    throw new InvalidConfigurationPropertyValueException("processors.tags.id", tag.getId(), "The tag ID exists in more than one tag ID, please update the tag ID for processor: " + processor.getId());
                }
                cacheMap.put(tag.getId(), Caffeine.newBuilder().build());
            }
        }

        return cacheMap;
    }

    @Bean
    public Cache<String, List<String>> topicCache() {
        return Caffeine.newBuilder().build();
    }

    @Bean
    public Cache<String, MessageResource> messageCache() {
        return Caffeine.newBuilder().build();
    }

    @Bean
    public Queue<MessageResource> latestMessageCache() {
        return create(50);
    }

    @Scheduled(cron = "0 0 0 * * SAT")
    public void evictCaches() {
        topicCache().invalidateAll();
        messageCache().invalidateAll();
        Collection<Cache<String, MessageBundle>> cacheCollection = cacheMap().values();
        for (Cache<String, MessageBundle> messageBundleCache : cacheCollection) {
            messageBundleCache.invalidateAll();
        }
    }
}

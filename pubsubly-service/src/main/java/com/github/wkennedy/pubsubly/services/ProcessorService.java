package com.github.wkennedy.pubsubly.services;

import com.github.wkennedy.pubsubly.api.Tag;
import com.github.wkennedy.pubsubly.config.PluginProcessorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProcessorService {

    private final PluginProcessorProperties pluginProcessorProperties;

    public ProcessorService(PluginProcessorProperties pluginProcessorProperties) {
        this.pluginProcessorProperties = pluginProcessorProperties;
    }

    public List<Tag> getTags() {
        List<Tag> tags = new ArrayList<>(pluginProcessorProperties.getTags());
        List<PluginProcessorProperties.Processor> processors = pluginProcessorProperties.getProcessors();
        for (PluginProcessorProperties.Processor processor : processors) {
            tags.addAll(processor.getTags());
        }

        return tags;
    }

    public Map<String, Tag> getTagsAsMap() {
        List<Tag> tags = getTags();

        Map<String, Tag> tagsMap = new HashMap<>();
        for (Tag tag : tags) {
            tagsMap.put(tag.getId(), tag);
        }

        return tagsMap;
    }

    public Tag getMessageCorrelationId() {
        for(Tag tag : getTags()) {
            if(tag.isMessageCorrelationId()) {
                return tag;
            }
        }
        return null;
    }

    public Tag getPrimaryMessageId() {
        for(Tag tag : getTags()) {
            if(tag.isPrimaryMessageId()) {
                return tag;
            }
        }
        return null;
    }
}

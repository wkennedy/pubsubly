package com.github.wkennedy.pubsubly.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.github.wkennedy.pubsubly.api.Tag;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "plugin-processors")
public class PluginProcessorProperties {

    private List<Tag> tags = new ArrayList<>();

    private List<Processor> processors = new ArrayList<>();

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Processor> getProcessors() {
        return processors;
    }

    public void setProcessors(List<Processor> processors) {
        this.processors = processors;
    }

    public static class Processor {
        private String id;
        private List<Tag> tags = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Tag> getTags() {
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = tags;
        }

        @Override
        public String toString() {
            return "Processor{" +
                    "id='" + id + '\'' +
                    ", tags=" + tags +
                    '}';
        }
    }


}

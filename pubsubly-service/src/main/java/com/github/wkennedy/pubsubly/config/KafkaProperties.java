package com.github.wkennedy.pubsubly.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties extends org.springframework.boot.autoconfigure.kafka.KafkaProperties {

    private String topicPattern;

    private String[] topicNames = new String[]{};

    public String getTopicPattern() {
        return topicPattern;
    }

    public void setTopicPattern(String topicPattern) {
        this.topicPattern = topicPattern;
    }

    public String[] getTopicNames() {
        return topicNames;
    }

    public void setTopicNames(String[] topicNames) {
        this.topicNames = topicNames;
    }
}

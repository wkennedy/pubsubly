package com.github.wkennedy.pubsubly.models;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.Serializable;

@JsonTypeName(value = "messageFlowStopover")
@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT, use= JsonTypeInfo.Id.NAME)
public class MessageFlowStopover implements Serializable {
    private String topic;
    private String previousTopic = "";
    private Long timestamp;
    private String timeSinceLastTopic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPreviousTopic() {
        return previousTopic;
    }

    public void setPreviousTopic(String previousTopic) {
        this.previousTopic = previousTopic;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeSinceLastTopic() {
        return timeSinceLastTopic;
    }

    public void setTimeSinceLastTopic(String timeSinceLastTopic) {
        this.timeSinceLastTopic = timeSinceLastTopic;
    }
}

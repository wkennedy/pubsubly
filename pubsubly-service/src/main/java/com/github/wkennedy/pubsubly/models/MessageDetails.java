package com.github.wkennedy.pubsubly.models;

import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class MessageDetails extends ResourceSupport {
    private MessageResourceBundle messageResourceBundle;
    private Long averageTimeBetweenTopics;
    private MessageFlow messageFlow;

    public MessageResourceBundle getMessageResourceBundle() {
        return messageResourceBundle;
    }

    public void setMessageResourceBundle(MessageResourceBundle messageResourceBundle) {
        this.messageResourceBundle = messageResourceBundle;
    }

    public Long getAverageTimeBetweenTopics() {
        return averageTimeBetweenTopics;
    }

    public void setAverageTimeBetweenTopics(Long averageTimeBetweenTopics) {
        this.averageTimeBetweenTopics = averageTimeBetweenTopics;
    }

    public MessageFlow getMessageFlow() {
        return messageFlow;
    }

    public void setMessageFlow(MessageFlow messageFlow) {
        this.messageFlow = messageFlow;
    }
}

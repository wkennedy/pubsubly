package com.github.wkennedy.pubsubly.models;

import org.springframework.hateoas.RepresentationModel;

public class MessageDetails extends RepresentationModel {
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

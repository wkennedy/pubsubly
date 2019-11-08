package com.github.wkennedy.pubsubly.models;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.messaging.Message;

import java.util.Map;

public class MessageResource extends ResourceSupport {

    private Message message;
    private Map<String, String> messageKeyMap;

    public MessageResource(Message message, Map<String, String> messageKeyMap) {
        this.message = message;
        this.messageKeyMap = messageKeyMap;
    }

    public Message getMessage() {
        return message;
    }

    public Map<String, String> getMessageKeyMap() {
        return messageKeyMap;
    }

    public void setMessageKeyMap(Map<String, String> messageKeyMap) {
        this.messageKeyMap = messageKeyMap;
    }

    public String getMessageKeyValue(String key) {
        return messageKeyMap.get(key);
    }

    @Override
    public String toString() {
        return "MessageResource{" +
                "message=" + message +
                ", messageKeyMap=" + messageKeyMap +
                '}';
    }
}

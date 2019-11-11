package com.github.wkennedy.pubsubly.models;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.messaging.Message;

import java.util.Map;

/**
 * A MessageResource is a wrapper around a Spring message with HATEOS support as well as a Map that contains
 * the Tag ID as a key and the value of that tag that was discovered during processing.
 */
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

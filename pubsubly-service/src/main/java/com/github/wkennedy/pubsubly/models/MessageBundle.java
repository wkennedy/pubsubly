package com.github.wkennedy.pubsubly.models;

import com.github.wkennedy.pubsubly.api.Tag;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A MessageBundle is a list of message UUIDs associated with a specific Tag.
 */
public class MessageBundle {

    private Tag tag;
    private Set<String> messageUUIDs = new HashSet<>();

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public void addMessageUUID(String uuid) {
        messageUUIDs.add(uuid);
    }

    public Set<String> getMessageUUIDs() {
        return messageUUIDs;
    }

    public void setMessageUUIDs(Set<String> messageUUIDs) {
        this.messageUUIDs = messageUUIDs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageBundle that = (MessageBundle) o;
        return Objects.equals(tag, that.tag) &&
                Objects.equals(messageUUIDs, that.messageUUIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, messageUUIDs);
    }
}

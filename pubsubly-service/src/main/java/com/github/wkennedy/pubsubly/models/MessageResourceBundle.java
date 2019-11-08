package com.github.wkennedy.pubsubly.models;

import com.github.wkennedy.pubsubly.api.Tag;

import java.util.List;

public class MessageResourceBundle {

    public MessageResourceBundle(Tag tag, List<MessageResource> messageResources) {
        this.tag = tag;
        this.messageResources = messageResources;
    }

    private Tag tag;
    private List<MessageResource> messageResources;

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public List<MessageResource> getMessageResources() {
        return messageResources;
    }

    public void setMessageResources(List<MessageResource> messageResources) {
        this.messageResources = messageResources;
    }
}

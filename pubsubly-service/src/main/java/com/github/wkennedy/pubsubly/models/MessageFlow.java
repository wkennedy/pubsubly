package com.github.wkennedy.pubsubly.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageFlow implements Serializable {

    private String type;

    private String id;

    private Set<Node> nodes = new HashSet<>();

    private Set<Link> links = new HashSet<>();

    private List<MessageFlowStopover> messageFlowStopovers = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MessageFlowStopover> getMessageFlowStopovers() {
        return messageFlowStopovers;
    }

    public void setMessageFlowStopovers(List<MessageFlowStopover> messageFlowStopovers) {
        this.messageFlowStopovers = messageFlowStopovers;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }

    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }
}

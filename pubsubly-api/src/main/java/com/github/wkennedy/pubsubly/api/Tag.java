package com.github.wkennedy.pubsubly.api;

import java.util.Objects;

public class Tag {
    private String id;
    private String value;
    private String description;
    private Boolean isPrimaryMessageId = false;
    private Boolean isMessageCorrelationId = false;
    private Boolean display = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isPrimaryMessageId() {
        return isPrimaryMessageId;
    }

    public void setIsPrimaryMessageId(Boolean primaryMessageId) {
        isPrimaryMessageId = primaryMessageId;
    }

    public Boolean isMessageCorrelationId() {
        return isMessageCorrelationId;
    }

    public void setIsMessageCorrelationId(Boolean messageCorrelationId) {
        isMessageCorrelationId = messageCorrelationId;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id) &&
                Objects.equals(value, tag.value) &&
                Objects.equals(description, tag.description) &&
                Objects.equals(isPrimaryMessageId, tag.isPrimaryMessageId) &&
                Objects.equals(isMessageCorrelationId, tag.isMessageCorrelationId) &&
                Objects.equals(display, tag.display);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, description, isPrimaryMessageId, isMessageCorrelationId, display);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id='" + id + '\'' +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                ", isPrimaryMessageId=" + isPrimaryMessageId +
                ", isMessageCorrelationId=" + isMessageCorrelationId +
                ", display=" + display +
                '}';
    }
}
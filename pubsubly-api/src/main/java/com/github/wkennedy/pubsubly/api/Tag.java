package com.github.wkennedy.pubsubly.api;

import java.util.Objects;

/**
 * A tag is a way to define and describe the data that you are interested in tracking within the messages consumed.
 */
public class Tag {
    //The id is the unique identifier of this tag and is used internally for tracking messages
    private String id;
    //This is the value in the message that you want to tag and track, for example it might be a header key.
    private String value;
    //This is the description of the data you are tagging. It can be used for clients such as the UI.
    private String description;
    //Specifies if this value is used a the unique identification of the message
    private Boolean isPrimaryMessageId = false;
    //This is the id of the event that caused this message to be raised. Allows for tracking the root cause of the event.
    private Boolean isMessageCorrelationId = false;
    //Whether this tag should display in the UI or client
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
package com.github.wkennedy.pubsubly.models;

public class TextCount {
    private String text;
    private String count;

    public TextCount(String text, String count) {
        this.text = text;
        this.count = count;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}

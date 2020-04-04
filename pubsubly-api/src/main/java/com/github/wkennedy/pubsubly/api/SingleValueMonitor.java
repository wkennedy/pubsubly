package com.github.wkennedy.pubsubly.api;

import java.util.Objects;

public class SingleValueMonitor implements Monitor {
    private String value;
    private Priority priority = Priority.NORMAL;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleValueMonitor that = (SingleValueMonitor) o;
        return Objects.equals(value, that.value) &&
                priority == that.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, priority);
    }

    @Override
    public String toString() {
        return "SingleValueMonitor{" +
                "value='" + value + '\'' +
                ", priority=" + priority +
                '}';
    }
}

package com.github.wkennedy.pubsubly.api;

import java.util.Objects;

public class PatternValueMonitor implements Monitor {
    private String regexPattern;
    private Priority priority = Priority.NORMAL;

    public String getValue() {
        return regexPattern;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatternValueMonitor that = (PatternValueMonitor) o;
        return Objects.equals(regexPattern, that.regexPattern) &&
                priority == that.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(regexPattern, priority);
    }

    @Override
    public String toString() {
        return "PatternValueMonitor{" +
                "regexPattern='" + regexPattern + '\'' +
                ", priority=" + priority +
                '}';
    }
}

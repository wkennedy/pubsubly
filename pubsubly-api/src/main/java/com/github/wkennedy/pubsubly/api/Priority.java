package com.github.wkennedy.pubsubly.api;

public enum Priority {
    NORMAL(0),
    HIGH(3),
    MEDIUM(2),
    LOW(1);

    private Integer order;

    Priority(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public boolean isHighestOrder() {
        return this.order.equals(getHighestOrder());
    }

    public Integer getHighestOrder() {
        return HIGH.getOrder();
    }

    public static Priority getHighestPriority(Priority priority1, Priority priority2) {
        if(priority1.getOrder() > priority2.getOrder()) {
            return priority1;
        }

        return priority2;
    }
}

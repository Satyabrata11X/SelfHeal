package com.selfheal.starter.event;

import java.time.Instant;

public class SelfHealEvent {

    private final SelfHealEventType type;
    private final String componentName;
    private final Instant timestamp;
    private final String message;

    public SelfHealEvent(
            SelfHealEventType type,
            String componentName,
            String message) {

        this.type = type;
        this.componentName = componentName;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public SelfHealEventType getType() {
        return type;
    }

    public String getComponentName() {
        return componentName;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
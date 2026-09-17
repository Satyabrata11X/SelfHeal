package com.selfheal.starter.failure;

import java.time.Instant;

public class FailureInfo {

    private final FailureType type;
    private final String componentName;
    private final String message;
    private final Instant timestamp;

    public FailureInfo(
            FailureType type,
            String componentName,
            String message) {

        this.type = type;
        this.componentName = componentName;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public FailureType getType() {
        return type;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
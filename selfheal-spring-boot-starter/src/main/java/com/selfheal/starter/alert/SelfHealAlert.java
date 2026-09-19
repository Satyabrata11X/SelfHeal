package com.selfheal.starter.alert;

import com.selfheal.starter.event.SelfHealEventType;

import java.time.Instant;

public class SelfHealAlert {

    private final String componentName;
    private final SelfHealEventType eventType;
    private final AlertSeverity severity;
    private final String message;
    private final Instant timestamp;

    public SelfHealAlert(
            String componentName,
            SelfHealEventType eventType,
            AlertSeverity severity,
            String message) {

        if (componentName == null || componentName.isBlank()) {
            throw new IllegalArgumentException(
                    "Component name cannot be empty"
            );
        }

        if (eventType == null) {
            throw new IllegalArgumentException(
                    "Event type cannot be null"
            );
        }

        if (severity == null) {
            throw new IllegalArgumentException(
                    "Alert severity cannot be null"
            );
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Alert message cannot be empty"
            );
        }

        this.componentName = componentName;
        this.eventType = eventType;
        this.severity = severity;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public String getComponentName() {
        return componentName;
    }

    public SelfHealEventType getEventType() {
        return eventType;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
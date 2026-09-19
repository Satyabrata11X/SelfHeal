package com.selfheal.starter.audit;

import com.selfheal.starter.failure.FailureType;

import java.time.Instant;

public class RecoveryAuditEntry {

    private final String componentName;
    private final FailureType failureType;
    private final String strategy;
    private final int attempts;
    private final boolean successful;
    private final long duration;
    private final String message;
    private final Instant timestamp;

    public RecoveryAuditEntry(
            String componentName,
            FailureType failureType,
            String strategy,
            int attempts,
            boolean successful,
            long duration,
            String message) {

        if (componentName == null || componentName.isBlank()) {
            throw new IllegalArgumentException(
                    "Component name cannot be empty"
            );
        }

        if (failureType == null) {
            throw new IllegalArgumentException(
                    "Failure type cannot be null"
            );
        }

        if (strategy == null || strategy.isBlank()) {
            throw new IllegalArgumentException(
                    "Recovery strategy cannot be empty"
            );
        }

        if (attempts < 0) {
            throw new IllegalArgumentException(
                    "Attempts cannot be negative"
            );
        }

        if (duration < 0) {
            throw new IllegalArgumentException(
                    "Duration cannot be negative"
            );
        }

        this.componentName = componentName;
        this.failureType = failureType;
        this.strategy = strategy;
        this.attempts = attempts;
        this.successful = successful;
        this.duration = duration;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public String getComponentName() {
        return componentName;
    }

    public FailureType getFailureType() {
        return failureType;
    }

    public String getStrategy() {
        return strategy;
    }

    public int getAttempts() {
        return attempts;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public long getDuration() {
        return duration;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
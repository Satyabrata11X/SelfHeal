package com.selfheal.starter.recovery;

import com.selfheal.starter.failure.FailureType;

import java.time.Instant;

public class RecoveryEscalation {

    private final String componentName;

    private final FailureType failureType;

    private final String recoveryStrategy;

    private final int attempts;

    private final long duration;

    private final String reason;

    private final Instant timestamp;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RecoveryEscalation(
            String componentName,
            FailureType failureType,
            String recoveryStrategy,
            int attempts,
            long duration,
            String reason) {

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

        if (recoveryStrategy == null
                || recoveryStrategy.isBlank()) {

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
        this.recoveryStrategy = recoveryStrategy;
        this.attempts = attempts;
        this.duration = duration;
        this.reason = reason;
        this.timestamp = Instant.now();
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public String getComponentName() {
        return componentName;
    }

    public FailureType getFailureType() {
        return failureType;
    }

    public String getRecoveryStrategy() {
        return recoveryStrategy;
    }

    public int getAttempts() {
        return attempts;
    }

    public long getDuration() {
        return duration;
    }

    public String getReason() {
        return reason;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
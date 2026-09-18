package com.selfheal.starter.history;

import com.selfheal.starter.failure.FailureType;

import java.time.Instant;

public class RecoveryRecord {

    private final String componentName;

    private final FailureType failureType;

    private final String recoveryStrategy;

    private final int attempts;

    private final boolean successful;

    private final long duration;

    private final Instant timestamp;

    private final String message;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RecoveryRecord(
            String componentName,
            FailureType failureType,
            String recoveryStrategy,
            int attempts,
            boolean successful,
            long duration,
            String message) {

        this.componentName = componentName;
        this.failureType = failureType;
        this.recoveryStrategy = recoveryStrategy;
        this.attempts = attempts;
        this.successful = successful;
        this.duration = duration;
        this.timestamp = Instant.now();
        this.message = message;
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

    public boolean isSuccessful() {
        return successful;
    }

    public long getDuration() {
        return duration;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
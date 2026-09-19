package com.selfheal.starter.recovery;

public class CircuitBreakerConfig {

    private final int failureThreshold;
    private final long openDuration;
    private final int halfOpenMaxCalls;

    public CircuitBreakerConfig(
            int failureThreshold,
            long openDuration,
            int halfOpenMaxCalls
    ) {
        if (failureThreshold <= 0) {
            throw new IllegalArgumentException(
                    "Failure threshold must be greater than 0"
            );
        }

        if (openDuration <= 0) {
            throw new IllegalArgumentException(
                    "Open duration must be greater than 0"
            );
        }

        if (halfOpenMaxCalls <= 0) {
            throw new IllegalArgumentException(
                    "Half-open max calls must be greater than 0"
            );
        }

        this.failureThreshold = failureThreshold;
        this.openDuration = openDuration;
        this.halfOpenMaxCalls = halfOpenMaxCalls;
    }

    public int getFailureThreshold() {
        return failureThreshold;
    }

    public long getOpenDuration() {
        return openDuration;
    }

    public int getHalfOpenMaxCalls() {
        return halfOpenMaxCalls;
    }
}
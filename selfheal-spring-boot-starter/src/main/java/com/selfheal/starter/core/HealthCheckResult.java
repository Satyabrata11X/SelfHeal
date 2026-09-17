package com.selfheal.starter.core;

import com.selfheal.starter.failure.FailureType;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public class HealthCheckResult {

    private final HealthStatus status;
    private final FailureType failureType;
    private final String message;
    private final long responseTime;
    private final Instant timestamp;
    private final Map<String, Object> metadata;

    private HealthCheckResult(
            HealthStatus status,
            FailureType failureType,
            String message,
            long responseTime,
            Instant timestamp,
            Map<String, Object> metadata) {

        this.status = status;
        this.failureType = failureType;
        this.message = message;
        this.responseTime = responseTime;
        this.timestamp = timestamp;
        this.metadata = metadata;
    }

    public static HealthCheckResult up(long responseTime) {

        return new HealthCheckResult(
                HealthStatus.UP,
                null,
                "Health check successful",
                responseTime,
                Instant.now(),
                Collections.emptyMap()
        );
    }

    public static HealthCheckResult down(
            FailureType failureType,
            String message,
            long responseTime) {

        return new HealthCheckResult(
                HealthStatus.DOWN,
                failureType,
                message,
                responseTime,
                Instant.now(),
                Collections.emptyMap()
        );
    }

    public HealthStatus getStatus() {
        return status;
    }

    public FailureType getFailureType() {
        return failureType;
    }

    public String getMessage() {
        return message;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public boolean isHealthy() {
        return status == HealthStatus.UP;
    }
}
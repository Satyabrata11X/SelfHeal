package com.selfheal.starter.fingerprint;

import java.time.Instant;

public class FailureFingerprint {

    private final String fingerprint;
    private final String componentName;
    private final String exceptionType;
    private final String failureType;
    private final String location;
    private final Instant timestamp;

    public FailureFingerprint(
            String fingerprint,
            String componentName,
            String exceptionType,
            String failureType,
            String location) {

        if (fingerprint == null || fingerprint.isBlank()) {
            throw new IllegalArgumentException(
                    "Fingerprint cannot be empty"
            );
        }

        if (componentName == null || componentName.isBlank()) {
            throw new IllegalArgumentException(
                    "Component name cannot be empty"
            );
        }

        if (exceptionType == null || exceptionType.isBlank()) {
            throw new IllegalArgumentException(
                    "Exception type cannot be empty"
            );
        }

        if (failureType == null || failureType.isBlank()) {
            throw new IllegalArgumentException(
                    "Failure type cannot be empty"
            );
        }

        this.fingerprint = fingerprint;
        this.componentName = componentName;
        this.exceptionType = exceptionType;
        this.failureType = failureType;
        this.location = location;
        this.timestamp = Instant.now();
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public String getFailureType() {
        return failureType;
    }

    public String getLocation() {
        return location;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
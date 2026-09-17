package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.failure.FailureType;

public class RecoveryContext {

    private final HealthCheck healthCheck;
    private final HealthCheckResult healthCheckResult;
    private final FailureType failureType;
    private final String message;

    public RecoveryContext(
            HealthCheck healthCheck,
            HealthCheckResult healthCheckResult,
            FailureType failureType,
            String message) {

        this.healthCheck = healthCheck;
        this.healthCheckResult = healthCheckResult;
        this.failureType = failureType;
        this.message = message;
    }

    public HealthCheck getHealthCheck() {
        return healthCheck;
    }

    public HealthCheckResult getHealthCheckResult() {
        return healthCheckResult;
    }

    public FailureType getFailureType() {
        return failureType;
    }

    public String getMessage() {
        return message;
    }

    public String getComponentName() {
        return healthCheck.getName();
    }

    public long getResponseTime() {
        if (healthCheckResult == null) {
            return 0;
        }

        return healthCheckResult.getResponseTime();
    }
}
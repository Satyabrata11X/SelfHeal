package com.selfheal.starter.dependency;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.failure.FailureType;

public class DependencyHealthCheck implements HealthCheck {

    private final Dependency dependency;

    public DependencyHealthCheck(Dependency dependency) {

        if (dependency == null) {
            throw new IllegalArgumentException(
                    "Dependency cannot be null"
            );
        }

        this.dependency = dependency;
    }

    @Override
    public String getName() {
        return dependency.getName();
    }

    @Override
    public HealthCheckResult check() {

        if (dependency.isAvailable()) {
            return HealthCheckResult.up(
                    dependency.getResponseTime()
            );
        }

        FailureType failureType =
                switch (dependency.getType()) {

                    case DATABASE ->
                            FailureType.DATABASE_FAILURE;

                    case CACHE,
                         REST_API,
                         MESSAGE_BROKER,
                         EXTERNAL_SERVICE ->
                            FailureType.CONNECTION_ERROR;

                    default ->
                            FailureType.UNKNOWN;
                };

        return HealthCheckResult.down(
                failureType,
                dependency.getMessage(),
                dependency.getResponseTime()
        );
    }

    public Dependency getDependency() {
        return dependency;
    }
}
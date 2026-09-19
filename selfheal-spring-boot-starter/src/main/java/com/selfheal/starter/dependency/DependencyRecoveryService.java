package com.selfheal.starter.dependency;

import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.failure.FailureClassifier;
import com.selfheal.starter.failure.FailureInfo;
import com.selfheal.starter.failure.FailureType;
import com.selfheal.starter.recovery.RecoveryContext;
import com.selfheal.starter.recovery.RecoveryResult;
import com.selfheal.starter.recovery.SelfHealRecoveryEngine;

public class DependencyRecoveryService {

    private final DependencyRegistry dependencyRegistry;
    private final DependencyFailureDetector failureDetector;
    private final FailureClassifier failureClassifier;
    private final SelfHealRecoveryEngine recoveryEngine;

    public DependencyRecoveryService(
            DependencyRegistry dependencyRegistry,
            DependencyFailureDetector failureDetector,
            FailureClassifier failureClassifier,
            SelfHealRecoveryEngine recoveryEngine) {

        if (dependencyRegistry == null) {
            throw new IllegalArgumentException(
                    "Dependency registry cannot be null"
            );
        }

        if (failureDetector == null) {
            throw new IllegalArgumentException(
                    "Dependency failure detector cannot be null"
            );
        }

        if (failureClassifier == null) {
            throw new IllegalArgumentException(
                    "Failure classifier cannot be null"
            );
        }

        if (recoveryEngine == null) {
            throw new IllegalArgumentException(
                    "Recovery engine cannot be null"
            );
        }

        this.dependencyRegistry = dependencyRegistry;
        this.failureDetector = failureDetector;
        this.failureClassifier = failureClassifier;
        this.recoveryEngine = recoveryEngine;
    }

    public RecoveryResult recover(String dependencyName) {

        Dependency dependency =
                dependencyRegistry.get(dependencyName);

        if (dependency == null) {
            throw new IllegalArgumentException(
                    "Dependency not found: " + dependencyName
            );
        }

        if (dependency.isAvailable()) {
            return new RecoveryResult(true, 0, 0);
        }

        FailureType failureType =
                mapFailureType(dependency);

        FailureInfo failureInfo =
                new FailureInfo(
                        failureType,
                        dependency.getName(),
                        dependency.getMessage()
                );

        System.out.println(
                "[SELFHEAL-DEPENDENCY] Failure detected"
                        + " | dependency=" + dependency.getName()
                        + " | type=" + dependency.getType()
                        + " | failureType=" + failureInfo.getType()
        );

        /*
         * Dependency recovery currently uses a lightweight
         * HealthCheck adapter so the existing recovery engine
         * can be reused.
         */
        DependencyHealthCheck healthCheck =
                new DependencyHealthCheck(dependency);

        RecoveryContext context =
                new RecoveryContext(
                        healthCheck,
                        HealthCheckResult.down(
                                failureType,
                                dependency.getMessage(),
                                dependency.getResponseTime()
                        ),
                        failureType,
                        dependency.getMessage()
                );

        RecoveryResult result =
                recoveryEngine.recover(
                        healthCheck,
                        context
                );

        if (result.isSuccessful()) {
            dependency.setStatus(DependencyStatus.UP);
            dependency.setMessage("Dependency recovered");
        }

        return result;
    }

    private FailureType mapFailureType(
            Dependency dependency) {

        return switch (dependency.getType()) {

            case DATABASE ->
                    FailureType.DATABASE_FAILURE;

            case CACHE,
                 REST_API,
                 MESSAGE_BROKER,
                 EXTERNAL_SERVICE ->
                    FailureType.CONNECTION_ERROR;

            case FILE_SYSTEM ->
                    FailureType.UNKNOWN;

            case UNKNOWN ->
                    FailureType.UNKNOWN;
        };
    }

    public boolean hasDependencyFailures() {
        return failureDetector.hasFailures();
    }
}
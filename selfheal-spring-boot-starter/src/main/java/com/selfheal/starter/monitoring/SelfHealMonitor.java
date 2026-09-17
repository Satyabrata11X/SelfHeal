package com.selfheal.starter.monitoring;

import com.selfheal.starter.config.SelfHealProperties;
import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.event.SelfHealEvent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.event.SelfHealEventType;
import com.selfheal.starter.failure.FailureClassifier;
import com.selfheal.starter.failure.FailureInfo;
import com.selfheal.starter.failure.FailureType;
import com.selfheal.starter.recovery.RecoveryContext;
import com.selfheal.starter.recovery.SelfHealRecoveryEngine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SelfHealMonitor {

    private final HealthCheck healthCheck;
    private final SelfHealRecoveryEngine recoveryEngine;
    private final SelfHealEventPublisher eventPublisher;
    private final FailureClassifier failureClassifier;

    private final long interval;
    private final long latencyThreshold;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public SelfHealMonitor(
            HealthCheck healthCheck,
            SelfHealRecoveryEngine recoveryEngine,
            SelfHealEventPublisher eventPublisher,
            FailureClassifier failureClassifier,
            SelfHealProperties properties) {

        this.healthCheck = healthCheck;
        this.recoveryEngine = recoveryEngine;
        this.eventPublisher = eventPublisher;
        this.failureClassifier = failureClassifier;

        this.interval =
                properties.getMonitoring().getInterval();

        this.latencyThreshold =
                properties.getMonitoring()
                        .getLatencyThreshold();
    }

    public void start() {

        System.out.println(
                "[SELFHEAL] Monitor started:"
                        + " interval="
                        + interval
                        + "ms"
                        + ", latencyThreshold="
                        + latencyThreshold
                        + "ms"
        );

        scheduler.scheduleAtFixedRate(
                this::checkHealth,
                2,
                interval,
                TimeUnit.MILLISECONDS
        );
    }

    private void checkHealth() {

        try {

            // ------------------------------------------
            // Perform health check
            // ------------------------------------------

            HealthCheckResult result =
                    healthCheck.check();

            System.out.println(
                    "[SELFHEAL] "
                            + healthCheck.getName()
                            + " -> "
                            + result.getStatus()
                            + " | responseTime="
                            + result.getResponseTime()
                            + "ms"
            );

            // ------------------------------------------
            // Component Failure Detection
            // ------------------------------------------

            if (!result.isHealthy()) {

                FailureInfo failure =
                        failureClassifier.classify(
                                healthCheck,
                                result
                        );

                handleFailure(
                        failure,
                        result
                );

                return;
            }

            // ------------------------------------------
            // High Latency Detection
            // ------------------------------------------

            if (result.getResponseTime()
                    > latencyThreshold) {

                FailureInfo failure =
                        new FailureInfo(
                                FailureType.HIGH_LATENCY,
                                healthCheck.getName(),
                                "Response time "
                                        + result.getResponseTime()
                                        + "ms exceeded latency threshold "
                                        + latencyThreshold
                                        + "ms"
                        );

                handleFailure(
                        failure,
                        result
                );
            }

        } catch (Exception exception) {

            // ------------------------------------------
            // Exception-based Failure Detection
            // ------------------------------------------

            FailureInfo failure =
                    failureClassifier.classify(
                            healthCheck,
                            exception
                    );

            handleFailure(
                    failure,
                    null
            );
        }
    }

    private void handleFailure(
            FailureInfo failure,
            HealthCheckResult healthCheckResult) {

        // ------------------------------------------
        // Log Failure Classification
        // ------------------------------------------

        System.out.println(
                "[SELFHEAL] Failure classified:"
                        + " type="
                        + failure.getType()
                        + ", component="
                        + failure.getComponentName()
                        + ", message="
                        + failure.getMessage()
        );

        // ------------------------------------------
        // Publish FAILURE_CLASSIFIED event
        // ------------------------------------------

        eventPublisher.publish(
                new SelfHealEvent(
                        SelfHealEventType.FAILURE_CLASSIFIED,
                        failure.getComponentName(),
                        "Failure type: "
                                + failure.getType()
                                + " | "
                                + failure.getMessage()
                )
        );

        // ------------------------------------------
        // Publish FAILURE_DETECTED event
        // ------------------------------------------

        eventPublisher.publish(
                new SelfHealEvent(
                        SelfHealEventType.FAILURE_DETECTED,
                        failure.getComponentName(),
                        failure.getMessage()
                )
        );

        // ------------------------------------------
        // Create Recovery Context
        // ------------------------------------------

        RecoveryContext context =
                new RecoveryContext(
                        healthCheck,
                        healthCheckResult,
                        failure.getType(),
                        failure.getMessage()
                );

        // ------------------------------------------
        // Start Recovery
        // ------------------------------------------

        recoveryEngine.recover(
                healthCheck,
                context
        );
    }
}
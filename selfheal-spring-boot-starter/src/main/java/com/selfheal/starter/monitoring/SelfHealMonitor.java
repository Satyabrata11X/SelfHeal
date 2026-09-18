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
import com.selfheal.starter.history.RecoveryHistory;
import com.selfheal.starter.history.RecoveryRecord;
import com.selfheal.starter.metrics.SelfHealMetrics;
import com.selfheal.starter.recovery.RecoveryContext;
import com.selfheal.starter.recovery.RecoveryCooldown;
import com.selfheal.starter.recovery.RecoveryResult;
import com.selfheal.starter.recovery.RecoveryState;
import com.selfheal.starter.recovery.SelfHealRecoveryEngine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SelfHealMonitor {

    // =========================================================
    // DEPENDENCIES
    // =========================================================

    private final HealthCheck healthCheck;

    private final SelfHealRecoveryEngine recoveryEngine;

    private final SelfHealEventPublisher eventPublisher;

    private final FailureClassifier failureClassifier;

    private final RecoveryCooldown recoveryCooldown;

    private final RecoveryHistory recoveryHistory;

    private final SelfHealMetrics metrics;


    // =========================================================
    // MONITORING CONFIGURATION
    // =========================================================

    private final long interval;

    private final long latencyThreshold;


    // =========================================================
    // RECOVERY STATE
    // =========================================================

    private final AtomicReference<RecoveryState> recoveryState =
            new AtomicReference<>(
                    RecoveryState.HEALTHY
            );


    // =========================================================
    // SCHEDULER
    // =========================================================

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SelfHealMonitor(
            HealthCheck healthCheck,
            SelfHealRecoveryEngine recoveryEngine,
            SelfHealEventPublisher eventPublisher,
            FailureClassifier failureClassifier,
            SelfHealProperties properties,
            RecoveryCooldown recoveryCooldown,
            RecoveryHistory recoveryHistory,
            SelfHealMetrics metrics) {

        this.healthCheck = healthCheck;

        this.recoveryEngine = recoveryEngine;

        this.eventPublisher = eventPublisher;

        this.failureClassifier = failureClassifier;

        this.recoveryCooldown = recoveryCooldown;

        this.recoveryHistory = recoveryHistory;

        this.metrics = metrics;

        this.interval =
                properties.getMonitoring()
                        .getInterval();

        this.latencyThreshold =
                properties.getMonitoring()
                        .getLatencyThreshold();
    }


    // =========================================================
    // START MONITOR
    // =========================================================

    public void start() {

        System.out.println(
                "[SELFHEAL] Monitor started:"
                        + " interval="
                        + interval
                        + "ms"
                        + ", latencyThreshold="
                        + latencyThreshold
                        + "ms"
                        + ", cooldown="
                        + recoveryCooldown.getCooldownMillis()
                        + "ms"
        );

        scheduler.scheduleAtFixedRate(
                this::checkHealth,
                2,
                interval,
                TimeUnit.MILLISECONDS
        );
    }


    // =========================================================
    // HEALTH CHECK
    // =========================================================

    private void checkHealth() {

        try {

            // -------------------------------------------------
            // PERFORM HEALTH CHECK
            // -------------------------------------------------

            HealthCheckResult result =
                    healthCheck.check();


            // -------------------------------------------------
            // RECORD HEALTH CHECK METRIC
            // -------------------------------------------------

            metrics.recordHealthCheck(
                    result.isHealthy(),
                    result.getResponseTime()
            );


            System.out.println(
                    "[SELFHEAL] "
                            + healthCheck.getName()
                            + " -> "
                            + result.getStatus()
                            + " | responseTime="
                            + result.getResponseTime()
                            + "ms"
            );


            // -------------------------------------------------
            // COMPONENT FAILURE
            // -------------------------------------------------

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


            // -------------------------------------------------
            // HIGH LATENCY
            // -------------------------------------------------

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

            // -------------------------------------------------
            // EXCEPTION-BASED FAILURE
            // -------------------------------------------------

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


    // =========================================================
    // FAILURE HANDLING
    // =========================================================

    private void handleFailure(
            FailureInfo failure,
            HealthCheckResult healthCheckResult) {

        // -----------------------------------------------------
        // COOLDOWN CHECK
        // -----------------------------------------------------

        if (recoveryCooldown.isInCooldown()) {

            long remainingCooldown =
                    recoveryCooldown
                            .getRemainingCooldown();

            System.out.println(
                    "[SELFHEAL] Recovery skipped."
                            + " Cooldown active."
                            + " Remaining="
                            + remainingCooldown
                            + "ms"
            );

            return;
        }


        // -----------------------------------------------------
        // RECORD FAILURE METRIC
        // -----------------------------------------------------

        metrics.recordFailureDetected(
                failure.getType()
        );


        // -----------------------------------------------------
        // RECOVERY FAILED STATE RESET
        // -----------------------------------------------------

        recoveryState.compareAndSet(
                RecoveryState.RECOVERY_FAILED,
                RecoveryState.HEALTHY
        );


        // -----------------------------------------------------
        // PREVENT DUPLICATE RECOVERY
        // -----------------------------------------------------

        if (!recoveryState.compareAndSet(
                RecoveryState.HEALTHY,
                RecoveryState.RECOVERING)) {

            System.out.println(
                    "[SELFHEAL] Recovery already in progress. "
                            + "Skipping duplicate recovery trigger."
            );

            return;
        }


        // -----------------------------------------------------
        // FAILURE CLASSIFICATION LOG
        // -----------------------------------------------------

        System.out.println(
                "[SELFHEAL] Failure classified:"
                        + " type="
                        + failure.getType()
                        + ", component="
                        + failure.getComponentName()
                        + ", message="
                        + failure.getMessage()
        );


        // -----------------------------------------------------
        // FAILURE CLASSIFIED EVENT
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // FAILURE DETECTED EVENT
        // -----------------------------------------------------

        eventPublisher.publish(
                new SelfHealEvent(
                        SelfHealEventType.FAILURE_DETECTED,
                        failure.getComponentName(),
                        failure.getMessage()
                )
        );


        // -----------------------------------------------------
        // CREATE RECOVERY CONTEXT
        // -----------------------------------------------------

        RecoveryContext context =
                new RecoveryContext(
                        healthCheck,
                        healthCheckResult,
                        failure.getType(),
                        failure.getMessage()
                );


        // -----------------------------------------------------
        // EXECUTE RECOVERY
        // -----------------------------------------------------

        RecoveryResult recoveryResult =
                recoveryEngine.recover(
                        healthCheck,
                        context
                );


        // -----------------------------------------------------
        // RECORD RECOVERY METRIC
        // -----------------------------------------------------

        metrics.recordRecovery(
                recoveryResult.isSuccessful(),
                recoveryResult.getAttempts(),
                recoveryResult.getDuration()
        );


        // -----------------------------------------------------
        // START COOLDOWN
        // -----------------------------------------------------

        recoveryCooldown.startCooldown();


        // -----------------------------------------------------
        // CREATE RECOVERY HISTORY RECORD
        // -----------------------------------------------------

        RecoveryRecord record =
                new RecoveryRecord(
                        healthCheck.getName(),
                        failure.getType(),
                        recoveryEngine.getStrategyName(),
                        recoveryResult.getAttempts(),
                        recoveryResult.isSuccessful(),
                        recoveryResult.getDuration(),
                        failure.getMessage()
                );


        // -----------------------------------------------------
        // STORE RECOVERY HISTORY
        // -----------------------------------------------------

        recoveryHistory.record(
                record
        );


        // -----------------------------------------------------
        // LOG RECOVERY HISTORY
        // -----------------------------------------------------

        System.out.println(
                "[SELFHEAL] Recovery history recorded:"
                        + " success="
                        + recoveryResult.isSuccessful()
                        + ", attempts="
                        + recoveryResult.getAttempts()
                        + ", duration="
                        + recoveryResult.getDuration()
                        + "ms"
        );


        // -----------------------------------------------------
        // UPDATE RECOVERY STATE
        // -----------------------------------------------------

        if (recoveryResult.isSuccessful()) {

            recoveryState.set(
                    RecoveryState.HEALTHY
            );

            System.out.println(
                    "[SELFHEAL] Recovery state -> HEALTHY"
            );

        } else {

            recoveryState.set(
                    RecoveryState.RECOVERY_FAILED
            );

            System.out.println(
                    "[SELFHEAL] Recovery state -> RECOVERY_FAILED"
            );
        }
    }


    // =========================================================
    // GET RECOVERY STATE
    // =========================================================

    public RecoveryState getRecoveryState() {

        return recoveryState.get();
    }


    // =========================================================
    // GET REMAINING COOLDOWN
    // =========================================================

    public long getRemainingCooldown() {

        return recoveryCooldown
                .getRemainingCooldown();
    }


    // =========================================================
    // CHECK COOLDOWN
    // =========================================================

    public boolean isRecoveryCooldownActive() {

        return recoveryCooldown
                .isInCooldown();
    }


    // =========================================================
    // GET RECOVERY HISTORY
    // =========================================================

    public RecoveryHistory getRecoveryHistory() {

        return recoveryHistory;
    }


    // =========================================================
    // GET METRICS
    // =========================================================

    public SelfHealMetrics getMetrics() {

        return metrics;
    }


    // =========================================================
    // STOP MONITOR
    // =========================================================

    public void stop() {

        scheduler.shutdownNow();

        System.out.println(
                "[SELFHEAL] Monitor stopped."
        );
    }
}
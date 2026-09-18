package com.selfheal.starter.metrics;

import com.selfheal.starter.failure.FailureType;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

public class SelfHealMetricsBinder {

    private final SelfHealMetrics metrics;

    public SelfHealMetricsBinder(
            SelfHealMetrics metrics,
            MeterRegistry meterRegistry) {

        this.metrics = metrics;

        registerHealthMetrics(meterRegistry);
        registerFailureMetrics(meterRegistry);
        registerRecoveryMetrics(meterRegistry);
        registerLastValueMetrics(meterRegistry);
    }

    private void registerHealthMetrics(
            MeterRegistry registry) {

        Gauge.builder(
                        "selfheal.health.checks",
                        metrics,
                        SelfHealMetrics::getTotalHealthChecks
                )
                .description(
                        "Total number of SelfHeal health checks"
                )
                .register(registry);

        Gauge.builder(
                        "selfheal.health.checks.success",
                        metrics,
                        SelfHealMetrics::getSuccessfulHealthChecks
                )
                .description(
                        "Total successful SelfHeal health checks"
                )
                .register(registry);

        Gauge.builder(
                        "selfheal.health.checks.failure",
                        metrics,
                        SelfHealMetrics::getFailedHealthChecks
                )
                .description(
                        "Total failed SelfHeal health checks"
                )
                .register(registry);

        Gauge.builder(
                        "selfheal.health.checks.success.rate",
                        metrics,
                        SelfHealMetrics::getHealthCheckSuccessRate
                )
                .description(
                        "SelfHeal health check success rate"
                )
                .baseUnit("percent")
                .register(registry);
    }

    private void registerFailureMetrics(
            MeterRegistry registry) {

        Gauge.builder(
                        "selfheal.failures.detected",
                        metrics,
                        SelfHealMetrics::getTotalFailuresDetected
                )
                .description(
                        "Total failures detected by SelfHeal"
                )
                .register(registry);

        for (FailureType type :
                FailureType.values()) {

            Gauge.builder(
                            "selfheal.failures.by.type",
                            metrics,
                            currentMetrics ->
                                    currentMetrics
                                            .getFailureTypeCounts()
                                            .getOrDefault(
                                                    type,
                                                    0L
                                            )
                    )
                    .description(
                            "Failures detected by failure type"
                    )
                    .tag(
                            "failure_type",
                            type.name()
                    )
                    .register(registry);
        }
    }

    private void registerRecoveryMetrics(
            MeterRegistry registry) {

        Gauge.builder(
                        "selfheal.recovery.processes",
                        metrics,
                        SelfHealMetrics::getTotalRecoveryProcesses
                )
                .description(
                        "Total SelfHeal recovery processes"
                )
                .register(registry);

        Gauge.builder(
                        "selfheal.recovery.success",
                        metrics,
                        SelfHealMetrics::getSuccessfulRecoveries
                )
                .description(
                        "Successful SelfHeal recoveries"
                )
                .register(registry);

        Gauge.builder(
                        "selfheal.recovery.failure",
                        metrics,
                        SelfHealMetrics::getFailedRecoveries
                )
                .description(
                        "Failed SelfHeal recoveries"
                )
                .register(registry);

        Gauge.builder(
                        "selfheal.recovery.attempts",
                        metrics,
                        SelfHealMetrics::getTotalRecoveryAttempts
                )
                .description(
                        "Total SelfHeal recovery attempts"
                )
                .register(registry);

        Gauge.builder(
                        "selfheal.recovery.success.rate",
                        metrics,
                        SelfHealMetrics::getRecoverySuccessRate
                )
                .description(
                        "SelfHeal recovery success rate"
                )
                .baseUnit("percent")
                .register(registry);
    }

    private void registerLastValueMetrics(
            MeterRegistry registry) {

        Gauge.builder(
                        "selfheal.health.response.time",
                        metrics,
                        SelfHealMetrics::getLastResponseTime
                )
                .description(
                        "Last SelfHeal health check response time"
                )
                .baseUnit("milliseconds")
                .register(registry);

        Gauge.builder(
                        "selfheal.recovery.duration",
                        metrics,
                        SelfHealMetrics::getLastRecoveryDuration
                )
                .description(
                        "Last SelfHeal recovery duration"
                )
                .baseUnit("milliseconds")
                .register(registry);
    }
}
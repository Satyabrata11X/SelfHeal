package com.selfheal.starter.management;

import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.metrics.SelfHealMetrics;
import com.selfheal.starter.monitoring.SelfHealMonitor;
import com.selfheal.starter.recovery.CircuitBreaker;
import com.selfheal.starter.recovery.CircuitBreakerManager;
import com.selfheal.starter.recovery.CircuitBreakerState;
import com.selfheal.starter.recovery.RecoveryState;
import com.selfheal.starter.dependency.Dependency;
import com.selfheal.starter.dependency.DependencyFailureDetector;
import com.selfheal.starter.dependency.DependencyRegistry;

import java.util.List;
import java.util.Map;

import java.util.LinkedHashMap;
import java.util.Map;

public class SelfHealManagementService {

    private final SelfHealComponent component;

    private final SelfHealMonitor monitor;

    private final SelfHealMetrics metrics;

    private final CircuitBreakerManager circuitBreakerManager;

    private final DependencyRegistry dependencyRegistry;

    private final DependencyFailureDetector dependencyFailureDetector;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SelfHealManagementService(
            SelfHealComponent component,
            SelfHealMonitor monitor,
            SelfHealMetrics metrics,
            CircuitBreakerManager circuitBreakerManager,
            DependencyRegistry dependencyRegistry,
            DependencyFailureDetector dependencyFailureDetector) {

        this.component = component;
        this.monitor = monitor;
        this.metrics = metrics;
        this.circuitBreakerManager = circuitBreakerManager;
        this.dependencyRegistry = dependencyRegistry;
        this.dependencyFailureDetector = dependencyFailureDetector;
    }


    // =========================================================
    // CURRENT STATUS
    // =========================================================

    public SelfHealStatus getStatus() {

        RecoveryState recoveryState =
                monitor.getRecoveryState();

        boolean cooldownActive =
                monitor.isRecoveryCooldownActive();

        long remainingCooldown =
                monitor.getRemainingCooldown();

        return new SelfHealStatus(
                component.getName(),
                component.isHealthy(),
                recoveryState,
                cooldownActive,
                remainingCooldown
        );
    }


    // =========================================================
    // METRICS
    // =========================================================

    public SelfHealMetrics getMetrics() {

        return metrics;
    }


    // =========================================================
    // MONITOR
    // =========================================================

    public SelfHealMonitor getMonitor() {

        return monitor;
    }


    // =========================================================
    // CIRCUIT BREAKERS
    // =========================================================

    public Map<String, Map<String, Object>> getCircuitBreakers() {

        Map<String, Map<String, Object>> result =
                new LinkedHashMap<>();

        for (Map.Entry<String, CircuitBreaker> entry :
                circuitBreakerManager.getAll().entrySet()) {

            CircuitBreaker circuitBreaker =
                    entry.getValue();

            Map<String, Object> details =
                    new LinkedHashMap<>();

            details.put(
                    "state",
                    circuitBreaker.getState()
            );

            details.put(
                    "failureCount",
                    circuitBreaker.getFailureCount()
            );

            details.put(
                    "halfOpenCalls",
                    circuitBreaker.getHalfOpenCalls()
            );

            details.put(
                    "openedAt",
                    circuitBreaker.getOpenedAt()
            );

            result.put(
                    entry.getKey(),
                    details
            );
        }

        return result;
    }


    // =========================================================
    // CIRCUIT BREAKER STATE
    // =========================================================

    public CircuitBreakerState getCircuitBreakerState(
            String componentName) {

        return circuitBreakerManager.getState(
                componentName
        );
    }


    // =========================================================
    // RESET CIRCUIT BREAKER
    // =========================================================

    public void resetCircuitBreaker(
            String componentName) {

        circuitBreakerManager.reset(
                componentName
        );
    }

    // =========================================================
// DEPENDENCIES
// =========================================================

    public Map<String, Dependency> getDependencies() {

        return dependencyRegistry.getAll();
    }


// =========================================================
// FAILED DEPENDENCIES
// =========================================================

    public List<Dependency> getFailedDependencies() {

        return dependencyFailureDetector.getFailedDependencies();
    }


// =========================================================
// HEALTHY DEPENDENCIES
// =========================================================

    public List<Dependency> getHealthyDependencies() {

        return dependencyFailureDetector.getHealthyDependencies();
    }
}
package com.selfheal.selfheal_demo_app.controller;

import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.dependency.Dependency;
import com.selfheal.starter.dependency.DependencyRegistry;
import com.selfheal.starter.dependency.DependencyRecoveryService;
import com.selfheal.starter.dependency.DependencyStatus;
import com.selfheal.starter.dependency.DependencyType;
import com.selfheal.starter.failure.FailureType;
import com.selfheal.starter.history.RecoveryHistory;
import com.selfheal.starter.history.RecoveryRecord;
import com.selfheal.starter.metrics.SelfHealMetrics;

import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/selfheal/test")
public class SelfHealTestController {

    private final SelfHealComponent component;

    private final RecoveryHistory recoveryHistory;

    private final SelfHealMetrics metrics;

    private final DependencyRegistry dependencyRegistry;

    private final DependencyRecoveryService dependencyRecoveryService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SelfHealTestController(
            SelfHealComponent component,
            RecoveryHistory recoveryHistory,
            SelfHealMetrics metrics,
            DependencyRegistry dependencyRegistry,
            DependencyRecoveryService dependencyRecoveryService) {

        this.component = component;

        this.recoveryHistory = recoveryHistory;

        this.metrics = metrics;

        this.dependencyRegistry = dependencyRegistry;

        this.dependencyRecoveryService = dependencyRecoveryService;

        registerDemoDependencies();
    }


    // =========================================================
    // REGISTER DEMO DEPENDENCIES
    // =========================================================

    private void registerDemoDependencies() {

        registerDependency(
                "postgresql",
                DependencyType.DATABASE
        );

        registerDependency(
                "redis",
                DependencyType.CACHE
        );

        registerDependency(
                "payment-api",
                DependencyType.REST_API
        );
    }


    private void registerDependency(
            String name,
            DependencyType type) {

        if (dependencyRegistry.contains(name)) {
            return;
        }

        Dependency dependency =
                new Dependency(
                        name,
                        type
                );

        dependency.setStatus(
                DependencyStatus.UP
        );

        dependency.setResponseTime(0);

        dependency.setMessage(
                "Dependency is healthy"
        );

        dependencyRegistry.register(
                dependency
        );
    }


    // =========================================================
    // COMPONENT STATUS
    // =========================================================

    @GetMapping("/status")
    public HealthCheckResult status() {

        return component.check();
    }


    // =========================================================
    // SIMULATE COMPONENT FAILURE
    // =========================================================

    @PostMapping("/fail")
    public String fail() {

        component.fail();

        return "SELFHEAL COMPONENT: FAILURE SIMULATED";
    }


    // =========================================================
    // MANUAL COMPONENT RECOVERY
    // =========================================================

    @PostMapping("/recover")
    public String recover() {

        component.recover();

        return "SELFHEAL COMPONENT: MANUALLY RECOVERED";
    }


    // =========================================================
    // SET SIMULATED LATENCY
    // =========================================================

    @PostMapping("/latency/{milliseconds}")
    public String setLatency(
            @PathVariable long milliseconds) {

        component.setSimulatedLatency(
                milliseconds
        );

        return "SELFHEAL COMPONENT: SIMULATED LATENCY SET TO "
                + milliseconds
                + "ms";
    }


    // =========================================================
    // CLEAR SIMULATED LATENCY
    // =========================================================

    @DeleteMapping("/latency")
    public String clearLatency() {

        component.clearSimulatedLatency();

        return "SELFHEAL COMPONENT: SIMULATED LATENCY CLEARED";
    }


    // =========================================================
    // GET SIMULATED LATENCY
    // =========================================================

    @GetMapping("/latency")
    public String getLatency() {

        return "Current simulated latency: "
                + component.getSimulatedLatency()
                + "ms";
    }


    // =========================================================
    // SIMULATE FAILURE BY TYPE
    // =========================================================

    @PostMapping("/failure/{type}")
    public String simulateFailure(
            @PathVariable String type) {

        FailureType failureType;

        try {

            failureType =
                    FailureType.valueOf(
                            type.toUpperCase()
                    );

        } catch (IllegalArgumentException e) {

            return "Invalid failure type: " + type;
        }

        component.simulateFailure(
                failureType
        );

        return "SELFHEAL COMPONENT: SIMULATED FAILURE = "
                + failureType;
    }


    // =========================================================
    // CLEAR SIMULATED FAILURE
    // =========================================================

    @DeleteMapping("/failure")
    public String clearFailure() {

        component.clearSimulatedFailure();

        return "SELFHEAL COMPONENT: SIMULATED FAILURE CLEARED";
    }


    // =========================================================
    // SIMULATE RECOVERY FAILURES
    // =========================================================

    @PostMapping("/recovery-failures/{count}")
    public String simulateRecoveryFailures(
            @PathVariable int count) {

        component.setSimulatedRecoveryFailures(
                count
        );

        return "SELFHEAL COMPONENT: SIMULATED "
                + count
                + " RECOVERY FAILURES";
    }


    // =========================================================
    // GET REMAINING RECOVERY FAILURES
    // =========================================================

    @GetMapping("/recovery-failures")
    public String getRecoveryFailures() {

        return "Remaining simulated recovery failures: "
                + component.getSimulatedRecoveryFailures();
    }


    // =========================================================
    // DEPENDENCY STATUS
    // =========================================================

    @GetMapping("/dependencies")
    public Map<String, Dependency> dependencyStatus() {

        return dependencyRegistry.getAll();
    }


    // =========================================================
    // SIMULATE DEPENDENCY UP
    // =========================================================

    @PostMapping("/dependencies/{name}/up")
    public String dependencyUp(
            @PathVariable String name) {

        Dependency dependency =
                dependencyRegistry.get(name);

        if (dependency == null) {

            return "Dependency not found: " + name;
        }

        dependency.setStatus(
                DependencyStatus.UP
        );

        dependency.setMessage(
                "Dependency is healthy"
        );

        return "DEPENDENCY "
                + name
                + ": UP";
    }


    // =========================================================
    // SIMULATE DEPENDENCY DOWN
    // =========================================================

    @PostMapping("/dependencies/{name}/down")
    public String dependencyDown(
            @PathVariable String name) {

        Dependency dependency =
                dependencyRegistry.get(name);

        if (dependency == null) {

            return "Dependency not found: " + name;
        }

        dependency.setStatus(
                DependencyStatus.DOWN
        );

        dependency.setMessage(
                "Dependency is unavailable"
        );

        return "DEPENDENCY "
                + name
                + ": DOWN";
    }


    // =========================================================
    // SIMULATE DEPENDENCY DEGRADED
    // =========================================================

    @PostMapping("/dependencies/{name}/degraded")
    public String dependencyDegraded(
            @PathVariable String name) {

        Dependency dependency =
                dependencyRegistry.get(name);

        if (dependency == null) {

            return "Dependency not found: " + name;
        }

        dependency.setStatus(
                DependencyStatus.DEGRADED
        );

        dependency.setMessage(
                "Dependency response is degraded"
        );

        return "DEPENDENCY "
                + name
                + ": DEGRADED";
    }


    // =========================================================
    // RECOVER DEPENDENCY
    // =========================================================

    @PostMapping("/dependencies/{name}/recover")
    public String recoverDependency(
            @PathVariable String name) {

        Dependency dependency =
                dependencyRegistry.get(name);

        if (dependency == null) {

            return "Dependency not found: " + name;
        }

        if (dependency.isAvailable()) {

            return "DEPENDENCY "
                    + name
                    + ": ALREADY UP";
        }

        var result =
                dependencyRecoveryService.recover(name);

        if (result.isSuccessful()) {

            return "DEPENDENCY "
                    + name
                    + ": RECOVERED"
                    + " | attempts="
                    + result.getAttempts()
                    + " | duration="
                    + result.getDuration()
                    + "ms";
        }

        return "DEPENDENCY "
                + name
                + ": RECOVERY FAILED"
                + " | attempts="
                + result.getAttempts()
                + " | duration="
                + result.getDuration()
                + "ms";
    }


    // =========================================================
    // GET COMPLETE RECOVERY HISTORY
    // =========================================================

    @GetMapping("/history")
    public List<RecoveryRecord> getRecoveryHistory() {

        return recoveryHistory.getRecords();
    }


    // =========================================================
    // GET RECOVERY STATISTICS
    // =========================================================

    @GetMapping("/statistics")
    public Map<String, Object> getRecoveryStatistics() {

        Map<String, Object> statistics =
                new LinkedHashMap<>();

        statistics.put(
                "totalRecoveryProcesses",
                recoveryHistory
                        .getTotalRecoveryProcesses()
        );

        statistics.put(
                "successfulRecoveries",
                recoveryHistory
                        .getSuccessfulRecoveries()
        );

        statistics.put(
                "failedRecoveries",
                recoveryHistory
                        .getFailedRecoveries()
        );

        statistics.put(
                "totalAttempts",
                recoveryHistory
                        .getTotalAttempts()
        );

        statistics.put(
                "successRate",
                recoveryHistory
                        .getSuccessRate()
        );

        statistics.put(
                "failureTypeCounts",
                recoveryHistory
                        .getFailureTypeCounts()
        );

        return statistics;
    }


    // =========================================================
    // CLEAR RECOVERY HISTORY
    // =========================================================

    @DeleteMapping("/history")
    public String clearRecoveryHistory() {

        recoveryHistory.clear();

        return "SELFHEAL RECOVERY HISTORY CLEARED";
    }


    // =========================================================
    // METRICS
    // =========================================================

    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "totalHealthChecks",
                metrics.getTotalHealthChecks()
        );

        result.put(
                "successfulHealthChecks",
                metrics.getSuccessfulHealthChecks()
        );

        result.put(
                "failedHealthChecks",
                metrics.getFailedHealthChecks()
        );

        result.put(
                "healthCheckSuccessRate",
                metrics.getHealthCheckSuccessRate()
        );

        result.put(
                "totalFailuresDetected",
                metrics.getTotalFailuresDetected()
        );

        result.put(
                "totalRecoveryProcesses",
                metrics.getTotalRecoveryProcesses()
        );

        result.put(
                "successfulRecoveries",
                metrics.getSuccessfulRecoveries()
        );

        result.put(
                "failedRecoveries",
                metrics.getFailedRecoveries()
        );

        result.put(
                "totalRecoveryAttempts",
                metrics.getTotalRecoveryAttempts()
        );

        result.put(
                "recoverySuccessRate",
                metrics.getRecoverySuccessRate()
        );

        result.put(
                "lastResponseTime",
                metrics.getLastResponseTime()
        );

        result.put(
                "lastRecoveryDuration",
                metrics.getLastRecoveryDuration()
        );

        result.put(
                "lastFailureType",
                metrics.getLastFailureType()
        );

        return result;
    }
}
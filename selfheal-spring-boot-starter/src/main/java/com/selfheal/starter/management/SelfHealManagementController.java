package com.selfheal.starter.management;

import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.dependency.Dependency;
import com.selfheal.starter.dependency.DependencyFailureDetector;
import com.selfheal.starter.dependency.DependencyRegistry;
import com.selfheal.starter.history.RecoveryHistory;
import com.selfheal.starter.history.RecoveryRecord;
import com.selfheal.starter.metrics.SelfHealMetrics;
import com.selfheal.starter.monitoring.SelfHealMonitor;
import com.selfheal.starter.recovery.CircuitBreaker;
import com.selfheal.starter.recovery.CircuitBreakerManager;
import com.selfheal.starter.recovery.RecoveryEscalation;
import com.selfheal.starter.recovery.RecoveryEscalationHandler;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/selfheal/management")
public class SelfHealManagementController {

    // =========================================================
    // DEPENDENCIES
    // =========================================================

    private final SelfHealComponent component;

    private final SelfHealMonitor monitor;

    private final SelfHealMetrics metrics;

    private final RecoveryHistory recoveryHistory;

    private final CircuitBreakerManager circuitBreakerManager;

    private final DependencyRegistry dependencyRegistry;

    private final DependencyFailureDetector dependencyFailureDetector;

    private final RecoveryEscalationHandler escalationHandler;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SelfHealManagementController(
            SelfHealComponent component,
            SelfHealMonitor monitor,
            SelfHealMetrics metrics,
            RecoveryHistory recoveryHistory,
            CircuitBreakerManager circuitBreakerManager,
            DependencyRegistry dependencyRegistry,
            DependencyFailureDetector dependencyFailureDetector,
            RecoveryEscalationHandler escalationHandler) {

        this.component = component;
        this.monitor = monitor;
        this.metrics = metrics;
        this.recoveryHistory = recoveryHistory;
        this.circuitBreakerManager = circuitBreakerManager;
        this.dependencyRegistry = dependencyRegistry;
        this.dependencyFailureDetector = dependencyFailureDetector;
        this.escalationHandler = escalationHandler;
    }


    // =========================================================
    // CURRENT STATUS
    // =========================================================

    @GetMapping("/status")
    public SelfHealStatus status() {

        return new SelfHealStatus(
                component.getName(),
                component.isHealthy(),
                monitor.getRecoveryState(),
                monitor.isRecoveryCooldownActive(),
                monitor.getRemainingCooldown()
        );
    }


    // =========================================================
    // CURRENT HEALTH
    // =========================================================

    @GetMapping("/health")
    public HealthCheckResult health() {

        return component.check();
    }


    // =========================================================
    // METRICS
    // =========================================================

    @GetMapping("/metrics")
    public Map<String, Object> metrics() {

        Map<String, Object> result =
                new LinkedHashMap<>();


        // -----------------------------------------------------
        // HEALTH CHECK METRICS
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // FAILURE METRICS
        // -----------------------------------------------------

        result.put(
                "totalFailuresDetected",
                metrics.getTotalFailuresDetected()
        );

        result.put(
                "lastFailureType",
                metrics.getLastFailureType()
        );

        result.put(
                "failureTypeCounts",
                metrics.getFailureTypeCounts()
        );


        // -----------------------------------------------------
        // RECOVERY METRICS
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // LAST OBSERVED VALUES
        // -----------------------------------------------------

        result.put(
                "lastResponseTime",
                metrics.getLastResponseTime()
        );

        result.put(
                "lastRecoveryDuration",
                metrics.getLastRecoveryDuration()
        );


        return result;
    }


    // =========================================================
    // CIRCUIT BREAKERS
    // =========================================================

    @GetMapping("/circuit-breakers")
    public Map<String, Map<String, Object>> circuitBreakers() {

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
    // RESET CIRCUIT BREAKER
    // =========================================================

    @PostMapping("/circuit-breakers/{componentName}/reset")
    public Map<String, Object> resetCircuitBreaker(
            @PathVariable String componentName) {

        circuitBreakerManager.reset(
                componentName
        );

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "component",
                componentName
        );

        result.put(
                "state",
                circuitBreakerManager.getState(
                        componentName
                )
        );

        result.put(
                "message",
                "Circuit breaker reset successfully"
        );

        return result;
    }


    // =========================================================
    // RECOVERY HISTORY
    // =========================================================

    @GetMapping("/history")
    public List<RecoveryRecord> history() {

        return recoveryHistory.getRecords();
    }


    // =========================================================
    // RECOVERY STATISTICS
    // =========================================================

    @GetMapping("/statistics")
    public Map<String, Object> statistics() {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "totalRecoveryProcesses",
                recoveryHistory
                        .getTotalRecoveryProcesses()
        );

        result.put(
                "successfulRecoveries",
                recoveryHistory
                        .getSuccessfulRecoveries()
        );

        result.put(
                "failedRecoveries",
                recoveryHistory
                        .getFailedRecoveries()
        );

        result.put(
                "totalAttempts",
                recoveryHistory
                        .getTotalAttempts()
        );

        result.put(
                "successRate",
                recoveryHistory
                        .getSuccessRate()
        );

        result.put(
                "failureTypeCounts",
                recoveryHistory
                        .getFailureTypeCounts()
        );

        return result;
    }


    // =========================================================
    // CLEAR HISTORY
    // =========================================================

    @DeleteMapping("/history")
    public String clearHistory() {

        recoveryHistory.clear();

        return "SELFHEAL RECOVERY HISTORY CLEARED";
    }


    // =========================================================
    // RESET METRICS
    // =========================================================

    @DeleteMapping("/metrics")
    public String resetMetrics() {

        metrics.reset();

        return "SELFHEAL METRICS RESET";
    }


    // =========================================================
    // DEPENDENCIES
    // =========================================================

    @GetMapping("/dependencies")
    public Map<String, Dependency> dependencies() {

        return dependencyRegistry.getAll();
    }


    // =========================================================
    // FAILED DEPENDENCIES
    // =========================================================

    @GetMapping("/dependencies/failed")
    public List<Dependency> failedDependencies() {

        return dependencyFailureDetector
                .getFailedDependencies();
    }


    // =========================================================
    // HEALTHY DEPENDENCIES
    // =========================================================

    @GetMapping("/dependencies/healthy")
    public List<Dependency> healthyDependencies() {

        return dependencyFailureDetector
                .getHealthyDependencies();
    }


    // =========================================================
    // RECOVERY ESCALATIONS
    // =========================================================

    @GetMapping("/escalations")
    public List<RecoveryEscalation> escalations() {

        return escalationHandler.getEscalations();
    }


    // =========================================================
    // ESCALATION COUNT
    // =========================================================

    @GetMapping("/escalations/count")
    public Map<String, Object> escalationCount() {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "totalEscalations",
                escalationHandler.getEscalationCount()
        );

        return result;
    }


    // =========================================================
    // CLEAR ESCALATIONS
    // =========================================================

    @DeleteMapping("/escalations")
    public String clearEscalations() {

        escalationHandler.clear();

        return "SELFHEAL RECOVERY ESCALATIONS CLEARED";
    }
}
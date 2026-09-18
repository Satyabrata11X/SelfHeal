package com.selfheal.starter.management;

import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.history.RecoveryHistory;
import com.selfheal.starter.history.RecoveryRecord;
import com.selfheal.starter.metrics.SelfHealMetrics;
import com.selfheal.starter.monitoring.SelfHealMonitor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/selfheal/management")
public class SelfHealManagementController {

    private final SelfHealComponent component;

    private final SelfHealMonitor monitor;

    private final SelfHealMetrics metrics;

    private final RecoveryHistory recoveryHistory;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SelfHealManagementController(
            SelfHealComponent component,
            SelfHealMonitor monitor,
            SelfHealMetrics metrics,
            RecoveryHistory recoveryHistory) {

        this.component = component;

        this.monitor = monitor;

        this.metrics = metrics;

        this.recoveryHistory = recoveryHistory;
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
}
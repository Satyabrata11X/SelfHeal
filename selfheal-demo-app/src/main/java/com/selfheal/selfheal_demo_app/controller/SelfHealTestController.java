package com.selfheal.selfheal_demo_app.controller;

import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.failure.FailureType;
import com.selfheal.starter.history.RecoveryHistory;
import com.selfheal.starter.history.RecoveryRecord;

import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/selfheal/test")
public class SelfHealTestController {

    private final SelfHealComponent component;

    private final RecoveryHistory recoveryHistory;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SelfHealTestController(
            SelfHealComponent component,
            RecoveryHistory recoveryHistory) {

        this.component = component;
        this.recoveryHistory = recoveryHistory;
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
    // MANUAL RECOVERY
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
}
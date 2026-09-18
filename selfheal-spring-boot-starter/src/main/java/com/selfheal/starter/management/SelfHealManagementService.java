package com.selfheal.starter.management;

import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.metrics.SelfHealMetrics;
import com.selfheal.starter.monitoring.SelfHealMonitor;
import com.selfheal.starter.recovery.RecoveryState;

public class SelfHealManagementService {

    private final SelfHealComponent component;

    private final SelfHealMonitor monitor;

    private final SelfHealMetrics metrics;

    public SelfHealManagementService(
            SelfHealComponent component,
            SelfHealMonitor monitor,
            SelfHealMetrics metrics) {

        this.component = component;
        this.monitor = monitor;
        this.metrics = metrics;
    }

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

    public SelfHealMetrics getMetrics() {

        return metrics;
    }

    public SelfHealMonitor getMonitor() {

        return monitor;
    }
}
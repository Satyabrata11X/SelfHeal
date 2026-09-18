package com.selfheal.starter.management;

import com.selfheal.starter.recovery.RecoveryState;

public class SelfHealStatus {

    private final String componentName;

    private final boolean healthy;

    private final RecoveryState recoveryState;

    private final boolean cooldownActive;

    private final long remainingCooldown;

    public SelfHealStatus(
            String componentName,
            boolean healthy,
            RecoveryState recoveryState,
            boolean cooldownActive,
            long remainingCooldown) {

        this.componentName = componentName;
        this.healthy = healthy;
        this.recoveryState = recoveryState;
        this.cooldownActive = cooldownActive;
        this.remainingCooldown = remainingCooldown;
    }

    public String getComponentName() {
        return componentName;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public RecoveryState getRecoveryState() {
        return recoveryState;
    }

    public boolean isCooldownActive() {
        return cooldownActive;
    }

    public long getRemainingCooldown() {
        return remainingCooldown;
    }
}
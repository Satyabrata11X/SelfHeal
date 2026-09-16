package com.selfheal.starter.monitoring;

import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.recovery.SelfHealRecoveryEngine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SelfHealMonitor {

    private final SelfHealComponent component;
    private final SelfHealRecoveryEngine recoveryEngine;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public SelfHealMonitor(
            SelfHealComponent component,
            SelfHealRecoveryEngine recoveryEngine) {

        this.component = component;
        this.recoveryEngine = recoveryEngine;
    }

    public void start() {

        scheduler.scheduleAtFixedRate(
                this::checkHealth,
                2,
                5,
                TimeUnit.SECONDS
        );
    }

    private void checkHealth() {

        System.out.println(
                "[SELFHEAL] Health check -> "
                        + (component.isHealthy()
                        ? "HEALTHY"
                        : "FAILED")
        );

        if (!component.isHealthy()) {
            recoveryEngine.recover(component);
        }
    }
}
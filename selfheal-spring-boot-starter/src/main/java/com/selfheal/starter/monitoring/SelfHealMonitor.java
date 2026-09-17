package com.selfheal.starter.monitoring;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.HealthStatus;
import com.selfheal.starter.recovery.SelfHealRecoveryEngine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SelfHealMonitor {

    private final HealthCheck healthCheck;
    private final SelfHealRecoveryEngine recoveryEngine;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public SelfHealMonitor(
            HealthCheck healthCheck,
            SelfHealRecoveryEngine recoveryEngine) {

        this.healthCheck = healthCheck;
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

        HealthStatus status = healthCheck.check();

        System.out.println(
                "[SELFHEAL] "
                        + healthCheck.getName()
                        + " -> "
                        + status
        );

        if (status == HealthStatus.DOWN) {

            System.out.println(
                    "[SELFHEAL] Failure detected in "
                            + healthCheck.getName()
            );

            recoveryEngine.recover(healthCheck);
        }
    }
}
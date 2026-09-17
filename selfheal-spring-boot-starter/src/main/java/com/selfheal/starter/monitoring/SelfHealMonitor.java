package com.selfheal.starter.monitoring;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.HealthStatus;
import com.selfheal.starter.event.SelfHealEvent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.event.SelfHealEventType;
import com.selfheal.starter.recovery.SelfHealRecoveryEngine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SelfHealMonitor {

    private final HealthCheck healthCheck;
    private final SelfHealRecoveryEngine recoveryEngine;
    private final SelfHealEventPublisher eventPublisher;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public SelfHealMonitor(
            HealthCheck healthCheck,
            SelfHealRecoveryEngine recoveryEngine,
            SelfHealEventPublisher eventPublisher) {

        this.healthCheck = healthCheck;
        this.recoveryEngine = recoveryEngine;
        this.eventPublisher = eventPublisher;
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

            eventPublisher.publish(
                    new SelfHealEvent(
                            SelfHealEventType.FAILURE_DETECTED,
                            healthCheck.getName(),
                            "Health check reported DOWN"
                    )
            );

            recoveryEngine.recover(healthCheck);
        }
    }
}
package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.event.SelfHealEvent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.event.SelfHealEventType;

public class SelfHealRecoveryEngine {

    private final RecoveryStrategy recoveryStrategy;
    private final SelfHealEventPublisher eventPublisher;

    public SelfHealRecoveryEngine(
            RecoveryStrategy recoveryStrategy,
            SelfHealEventPublisher eventPublisher) {

        this.recoveryStrategy = recoveryStrategy;
        this.eventPublisher = eventPublisher;
    }

    public void recover(HealthCheck healthCheck) {

        String componentName = healthCheck.getName();

        eventPublisher.publish(
                new SelfHealEvent(
                        SelfHealEventType.RECOVERY_STARTED,
                        componentName,
                        "Recovery process started"
                )
        );

        System.out.println(
                "[SELFHEAL] Recovery requested for: "
                        + componentName
        );

        System.out.println(
                "[SELFHEAL] Strategy: "
                        + recoveryStrategy.getName()
        );

        boolean recovered =
                recoveryStrategy.recover(healthCheck);

        if (recovered) {

            eventPublisher.publish(
                    new SelfHealEvent(
                            SelfHealEventType.RECOVERY_SUCCESS,
                            componentName,
                            "Component recovered successfully"
                    )
            );

            System.out.println(
                    "[SELFHEAL] Component recovered successfully."
            );

        } else {

            eventPublisher.publish(
                    new SelfHealEvent(
                            SelfHealEventType.RECOVERY_FAILED,
                            componentName,
                            "Recovery failed"
                    )
            );

            System.out.println(
                    "[SELFHEAL] Component could not be recovered."
            );
        }
    }
}
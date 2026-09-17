package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.event.SelfHealEvent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.event.SelfHealEventType;

public class ImmediateRecoveryStrategy
        implements RecoveryStrategy {

    private final RecoveryAction recoveryAction;
    private final SelfHealEventPublisher eventPublisher;

    public ImmediateRecoveryStrategy(
            RecoveryAction recoveryAction,
            SelfHealEventPublisher eventPublisher) {

        this.recoveryAction = recoveryAction;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String getName() {
        return "IMMEDIATE";
    }

    @Override
    public boolean recover(
            HealthCheck healthCheck,
            RecoveryContext context) {

        String componentName =
                healthCheck.getName();

        eventPublisher.publish(
                new SelfHealEvent(
                        SelfHealEventType.RECOVERY_ATTEMPT,
                        componentName,
                        "Immediate recovery attempt"
                                + " | failureType="
                                + context.getFailureType()
                )
        );

        System.out.println(
                "[SELFHEAL] Executing immediate recovery..."
        );

        boolean actionSuccessful =
                recoveryAction.execute(
                        healthCheck,
                        context
                );

        if (!actionSuccessful) {

            System.out.println(
                    "[SELFHEAL] Immediate recovery action failed."
            );

            return false;
        }

        HealthCheckResult result =
                healthCheck.check();

        if (result.isHealthy()) {

            System.out.println(
                    "[SELFHEAL] Immediate recovery successful."
            );

            return true;
        }

        System.out.println(
                "[SELFHEAL] Immediate recovery failed."
        );

        return false;
    }
}
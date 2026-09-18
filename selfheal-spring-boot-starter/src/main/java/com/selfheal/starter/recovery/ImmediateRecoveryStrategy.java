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


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ImmediateRecoveryStrategy(
            RecoveryAction recoveryAction,
            SelfHealEventPublisher eventPublisher) {

        this.recoveryAction = recoveryAction;

        this.eventPublisher = eventPublisher;
    }


    // =========================================================
    // STRATEGY NAME
    // =========================================================

    @Override
    public String getName() {

        return "IMMEDIATE";
    }


    // =========================================================
    // RECOVERY
    // =========================================================

    @Override
    public RecoveryResult recover(
            HealthCheck healthCheck,
            RecoveryContext context) {

        String componentName =
                healthCheck.getName();

        long startTime =
                System.currentTimeMillis();


        // ------------------------------------------
        // Publish Recovery Attempt
        // ------------------------------------------

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


        // ------------------------------------------
        // Execute Recovery Action
        // ------------------------------------------

        boolean actionSuccessful =
                recoveryAction.execute(
                        healthCheck,
                        context
                );


        if (!actionSuccessful) {

            long duration =
                    System.currentTimeMillis()
                            - startTime;

            System.out.println(
                    "[SELFHEAL] Immediate recovery action failed."
            );

            return new RecoveryResult(
                    false,
                    1,
                    duration
            );
        }


        // ------------------------------------------
        // Verify Recovery
        // ------------------------------------------

        HealthCheckResult result =
                healthCheck.check();


        if (result.isHealthy()) {

            long duration =
                    System.currentTimeMillis()
                            - startTime;

            System.out.println(
                    "[SELFHEAL] Immediate recovery successful."
            );

            return new RecoveryResult(
                    true,
                    1,
                    duration
            );
        }


        // ------------------------------------------
        // Recovery Failed
        // ------------------------------------------

        long duration =
                System.currentTimeMillis()
                        - startTime;

        System.out.println(
                "[SELFHEAL] Immediate recovery failed."
        );

        return new RecoveryResult(
                false,
                1,
                duration
        );
    }
}
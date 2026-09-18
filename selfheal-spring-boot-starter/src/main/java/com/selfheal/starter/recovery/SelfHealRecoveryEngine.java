package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.event.SelfHealEvent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.event.SelfHealEventType;

public class SelfHealRecoveryEngine {

    private final RecoveryStrategy recoveryStrategy;

    private final SelfHealEventPublisher eventPublisher;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SelfHealRecoveryEngine(
            RecoveryStrategy recoveryStrategy,
            SelfHealEventPublisher eventPublisher) {

        this.recoveryStrategy = recoveryStrategy;

        this.eventPublisher = eventPublisher;
    }


    // =========================================================
    // RECOVERY
    // =========================================================

    public RecoveryResult recover(
            HealthCheck healthCheck,
            RecoveryContext context) {

        String componentName =
                healthCheck.getName();


        // -----------------------------------------------------
        // RECOVERY STARTED EVENT
        // -----------------------------------------------------

        eventPublisher.publish(
                new SelfHealEvent(
                        SelfHealEventType.RECOVERY_STARTED,
                        componentName,
                        "Recovery process started"
                                + " | failureType="
                                + context.getFailureType()
                )
        );


        // -----------------------------------------------------
        // LOG RECOVERY INFORMATION
        // -----------------------------------------------------

        System.out.println(
                "[SELFHEAL] Recovery requested for: "
                        + componentName
        );

        System.out.println(
                "[SELFHEAL] Failure type: "
                        + context.getFailureType()
        );

        System.out.println(
                "[SELFHEAL] Strategy: "
                        + recoveryStrategy.getName()
        );


        // -----------------------------------------------------
        // EXECUTE RECOVERY STRATEGY
        // -----------------------------------------------------

        RecoveryResult result =
                recoveryStrategy.recover(
                        healthCheck,
                        context
                );


        // -----------------------------------------------------
        // RECOVERY SUCCESS
        // -----------------------------------------------------

        if (result.isSuccessful()) {

            eventPublisher.publish(
                    new SelfHealEvent(
                            SelfHealEventType.RECOVERY_SUCCESS,
                            componentName,
                            "Component recovered successfully"
                                    + " | attempts="
                                    + result.getAttempts()
                                    + " | duration="
                                    + result.getDuration()
                                    + "ms"
                    )
            );

            System.out.println(
                    "[SELFHEAL] Component recovered successfully."
            );

            System.out.println(
                    "[SELFHEAL] Recovery attempts: "
                            + result.getAttempts()
            );

            System.out.println(
                    "[SELFHEAL] Recovery duration: "
                            + result.getDuration()
                            + "ms"
            );

        } else {

            // -------------------------------------------------
            // RECOVERY FAILURE
            // -------------------------------------------------

            eventPublisher.publish(
                    new SelfHealEvent(
                            SelfHealEventType.RECOVERY_FAILED,
                            componentName,
                            "Recovery failed"
                                    + " | attempts="
                                    + result.getAttempts()
                                    + " | duration="
                                    + result.getDuration()
                                    + "ms"
                    )
            );

            System.out.println(
                    "[SELFHEAL] Component could not be recovered."
            );

            System.out.println(
                    "[SELFHEAL] Recovery attempts: "
                            + result.getAttempts()
            );

            System.out.println(
                    "[SELFHEAL] Recovery duration: "
                            + result.getDuration()
                            + "ms"
            );
        }


        // -----------------------------------------------------
        // RETURN RECOVERY RESULT
        // -----------------------------------------------------

        return result;
    }


    // =========================================================
    // GET STRATEGY NAME
    // =========================================================

    public String getStrategyName() {

        return recoveryStrategy.getName();
    }
}
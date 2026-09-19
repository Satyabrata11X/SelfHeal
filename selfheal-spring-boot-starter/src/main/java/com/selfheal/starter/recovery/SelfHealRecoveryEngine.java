package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.event.SelfHealEvent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.event.SelfHealEventType;

public class SelfHealRecoveryEngine {

    private final RecoveryStrategy recoveryStrategy;

    private final SelfHealEventPublisher eventPublisher;

    private final CircuitBreakerManager circuitBreakerManager;

    private final RecoveryEscalationHandler escalationHandler;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SelfHealRecoveryEngine(
            RecoveryStrategy recoveryStrategy,
            SelfHealEventPublisher eventPublisher,
            CircuitBreakerManager circuitBreakerManager,
            RecoveryEscalationHandler escalationHandler) {

        if (recoveryStrategy == null) {
            throw new IllegalArgumentException(
                    "Recovery strategy cannot be null"
            );
        }

        if (eventPublisher == null) {
            throw new IllegalArgumentException(
                    "Event publisher cannot be null"
            );
        }

        if (circuitBreakerManager == null) {
            throw new IllegalArgumentException(
                    "Circuit breaker manager cannot be null"
            );
        }

        if (escalationHandler == null) {
            throw new IllegalArgumentException(
                    "Recovery escalation handler cannot be null"
            );
        }

        this.recoveryStrategy = recoveryStrategy;
        this.eventPublisher = eventPublisher;
        this.circuitBreakerManager = circuitBreakerManager;
        this.escalationHandler = escalationHandler;
    }


    // =========================================================
    // RECOVERY
    // =========================================================

    public RecoveryResult recover(
            HealthCheck healthCheck,
            RecoveryContext context) {

        if (healthCheck == null) {
            throw new IllegalArgumentException(
                    "Health check cannot be null"
            );
        }

        if (context == null) {
            throw new IllegalArgumentException(
                    "Recovery context cannot be null"
            );
        }

        String componentName =
                healthCheck.getName();


        // -----------------------------------------------------
        // CIRCUIT BREAKER CHECK
        // -----------------------------------------------------

        if (!circuitBreakerManager.allowRequest(componentName)) {

            System.out.println(
                    "[SELFHEAL-CIRCUIT] Recovery blocked for: "
                            + componentName
                            + " | state="
                            + circuitBreakerManager.getState(
                            componentName
                    )
            );

            eventPublisher.publish(
                    new SelfHealEvent(
                            SelfHealEventType.RECOVERY_FAILED,
                            componentName,
                            "Recovery blocked by circuit breaker"
                                    + " | state="
                                    + circuitBreakerManager.getState(
                                    componentName
                            )
                    )
            );

            return new RecoveryResult(
                    false,
                    0,
                    0
            );
        }


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
        // UPDATE CIRCUIT BREAKER
        // -----------------------------------------------------

        if (result.isSuccessful()) {

            circuitBreakerManager.recordSuccess(
                    componentName
            );

        } else {

            circuitBreakerManager.recordFailure(
                    componentName
            );
        }


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


            // -------------------------------------------------
            // RECOVERY ESCALATION
            // -------------------------------------------------

            escalationHandler.escalate(
                    componentName,
                    context,
                    result,
                    recoveryStrategy.getName()
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


    // =========================================================
    // GET CIRCUIT BREAKER MANAGER
    // =========================================================

    public CircuitBreakerManager getCircuitBreakerManager() {

        return circuitBreakerManager;
    }


    // =========================================================
    // GET ESCALATION HANDLER
    // =========================================================

    public RecoveryEscalationHandler getEscalationHandler() {

        return escalationHandler;
    }
}
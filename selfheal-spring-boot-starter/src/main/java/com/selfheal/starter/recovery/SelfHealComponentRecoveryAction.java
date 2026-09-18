package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.failure.FailureType;

public class SelfHealComponentRecoveryAction
        implements RecoveryAction {

    @Override
    public String getName() {
        return "COMPONENT_RECOVERY";
    }

    @Override
    public boolean execute(
            HealthCheck healthCheck,
            RecoveryContext context) {

        // ------------------------------------------
        // Validate Health Check Component
        // ------------------------------------------

        if (!(healthCheck
                instanceof SelfHealComponent component)) {

            System.out.println(
                    "[SELFHEAL] No recovery action available for: "
                            + healthCheck.getName()
            );

            return false;
        }

        // ------------------------------------------
        // Simulate Recovery Failure
        // ------------------------------------------
        /*
         * This is used only by the demo/testing environment.
         *
         * If recovery failures have been configured,
         * this recovery attempt intentionally fails.
         *
         * This allows us to test:
         *
         * Attempt 1 → FAILED
         * Attempt 2 → FAILED
         * Attempt 3 → SUCCESS
         */

        if (component.consumeSimulatedRecoveryFailure()) {

            System.out.println(
                    "[SELFHEAL] Simulated recovery action failure."
            );

            return false;
        }

        // ------------------------------------------
        // Get Failure Type
        // ------------------------------------------

        FailureType failureType =
                context.getFailureType();

        System.out.println(
                "[SELFHEAL] Recovery action selected for: "
                        + failureType
        );

        // ------------------------------------------
        // Failure-Specific Recovery
        // ------------------------------------------

        switch (failureType) {

            // --------------------------------------
            // Component Failure
            // --------------------------------------

            case COMPONENT_FAILURE -> {

                System.out.println(
                        "[SELFHEAL] Recovering failed component..."
                );

                component.recover();

                return component.isHealthy();
            }

            // --------------------------------------
            // High Latency
            // --------------------------------------

            case HIGH_LATENCY -> {

                System.out.println(
                        "[SELFHEAL] Clearing simulated latency..."
                );

                component.clearSimulatedLatency();

                return true;
            }

            // --------------------------------------
            // Timeout
            // --------------------------------------

            case TIMEOUT -> {

                System.out.println(
                        "[SELFHEAL] Resetting simulated timeout..."
                );

                component.clearSimulatedFailure();

                return true;
            }

            // --------------------------------------
            // Connection Error
            // --------------------------------------

            case CONNECTION_ERROR -> {

                System.out.println(
                        "[SELFHEAL] Resetting simulated connection failure..."
                );

                component.clearSimulatedFailure();

                return true;
            }

            // --------------------------------------
            // Database Failure
            // --------------------------------------

            case DATABASE_FAILURE -> {

                System.out.println(
                        "[SELFHEAL] Resetting simulated database failure..."
                );

                component.clearSimulatedFailure();

                return true;
            }

            // --------------------------------------
            // Unknown Failure
            // --------------------------------------

            case UNKNOWN -> {

                System.out.println(
                        "[SELFHEAL] No specialized recovery action "
                                + "available for UNKNOWN failure."
                );

                return false;
            }

            // --------------------------------------
            // Safety Fallback
            // --------------------------------------

            default -> {

                System.out.println(
                        "[SELFHEAL] No specialized recovery action for: "
                                + failureType
                );

                return false;
            }
        }
    }
}
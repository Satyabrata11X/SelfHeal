package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.dependency.Dependency;
import com.selfheal.starter.dependency.DependencyHealthCheck;
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

        // =====================================================
        // DEPENDENCY RECOVERY
        // =====================================================

        if (healthCheck instanceof DependencyHealthCheck dependencyHealthCheck) {

            Dependency dependency =
                    dependencyHealthCheck.getDependency();

            System.out.println(
                    "[SELFHEAL] Recovering dependency: "
                            + dependency.getName()
                            + " | type="
                            + dependency.getType()
            );

            dependency.setStatus(
                    com.selfheal.starter.dependency.DependencyStatus.UP
            );

            dependency.setMessage(
                    "Dependency recovered"
            );

            System.out.println(
                    "[SELFHEAL] Dependency recovery action completed."
            );

            return dependency.isAvailable();
        }


        // =====================================================
        // SELFHEAL COMPONENT RECOVERY
        // =====================================================

        if (!(healthCheck instanceof SelfHealComponent component)) {

            System.out.println(
                    "[SELFHEAL] No recovery action available for: "
                            + healthCheck.getName()
            );

            return false;
        }


        // =====================================================
        // SIMULATED RECOVERY FAILURE
        // =====================================================

        if (component.consumeSimulatedRecoveryFailure()) {

            System.out.println(
                    "[SELFHEAL] Simulated recovery action failure."
            );

            return false;
        }


        // =====================================================
        // GET FAILURE TYPE
        // =====================================================

        FailureType failureType =
                context.getFailureType();

        System.out.println(
                "[SELFHEAL] Recovery action selected for: "
                        + failureType
        );


        // =====================================================
        // FAILURE-SPECIFIC COMPONENT RECOVERY
        // =====================================================

        switch (failureType) {

            case COMPONENT_FAILURE -> {

                System.out.println(
                        "[SELFHEAL] Recovering failed component..."
                );

                component.recover();

                return component.isHealthy();
            }

            case HIGH_LATENCY -> {

                System.out.println(
                        "[SELFHEAL] Clearing simulated latency..."
                );

                component.clearSimulatedLatency();

                return true;
            }

            case TIMEOUT -> {

                System.out.println(
                        "[SELFHEAL] Resetting simulated timeout..."
                );

                component.clearSimulatedFailure();

                return true;
            }

            case CONNECTION_ERROR -> {

                System.out.println(
                        "[SELFHEAL] Resetting simulated connection failure..."
                );

                component.clearSimulatedFailure();

                return true;
            }

            case DATABASE_FAILURE -> {

                System.out.println(
                        "[SELFHEAL] Resetting simulated database failure..."
                );

                component.clearSimulatedFailure();

                return true;
            }

            case UNKNOWN -> {

                System.out.println(
                        "[SELFHEAL] No specialized recovery action "
                                + "available for UNKNOWN failure."
                );

                return false;
            }

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
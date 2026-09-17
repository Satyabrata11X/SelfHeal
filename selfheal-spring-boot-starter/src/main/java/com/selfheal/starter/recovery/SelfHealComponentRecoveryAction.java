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

        if (!(healthCheck instanceof SelfHealComponent component)) {

            System.out.println(
                    "[SELFHEAL] No recovery action available for: "
                            + healthCheck.getName()
            );

            return false;
        }

        FailureType failureType =
                context.getFailureType();

        System.out.println(
                "[SELFHEAL] Recovery action selected for: "
                        + failureType
        );

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

            default -> {

                System.out.println(
                        "[SELFHEAL] No specialized recovery "
                                + "action for failure type: "
                                + failureType
                );

                return false;
            }
        }
    }
}
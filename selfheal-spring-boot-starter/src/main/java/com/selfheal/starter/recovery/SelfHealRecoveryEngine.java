package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;

public class SelfHealRecoveryEngine {

    private final RecoveryStrategy recoveryStrategy;

    public SelfHealRecoveryEngine(RecoveryStrategy recoveryStrategy) {
        this.recoveryStrategy = recoveryStrategy;
    }

    public void recover(HealthCheck healthCheck) {

        System.out.println(
                "[SELFHEAL] Recovery requested for: "
                        + healthCheck.getName()
        );

        System.out.println(
                "[SELFHEAL] Strategy: "
                        + recoveryStrategy.getName()
        );

        boolean recovered =
                recoveryStrategy.recover(healthCheck);

        if (recovered) {

            System.out.println(
                    "[SELFHEAL] Component recovered successfully."
            );

        } else {

            System.out.println(
                    "[SELFHEAL] Component could not be recovered."
            );
        }
    }
}
package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.HealthStatus;

public class RetryRecoveryStrategy implements RecoveryStrategy {

    private final int maxAttempts;
    private final RecoveryAction recoveryAction;

    public RetryRecoveryStrategy(
            int maxAttempts,
            RecoveryAction recoveryAction) {

        this.maxAttempts = maxAttempts;
        this.recoveryAction = recoveryAction;
    }

    @Override
    public String getName() {
        return "RETRY";
    }

    @Override
    public boolean recover(HealthCheck healthCheck) {

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            System.out.println(
                    "[SELFHEAL] Recovery attempt "
                            + attempt
                            + "/"
                            + maxAttempts
            );

            boolean actionSuccessful =
                    recoveryAction.execute(healthCheck);

            if (!actionSuccessful) {

                System.out.println(
                        "[SELFHEAL] Recovery action failed."
                );

                continue;
            }

            HealthStatus status = healthCheck.check();

            if (status == HealthStatus.UP) {

                System.out.println(
                        "[SELFHEAL] Recovery successful."
                );

                return true;
            }

            System.out.println(
                    "[SELFHEAL] Component still DOWN."
            );
        }

        System.out.println(
                "[SELFHEAL] Recovery failed after "
                        + maxAttempts
                        + " attempts."
        );

        return false;
    }
}
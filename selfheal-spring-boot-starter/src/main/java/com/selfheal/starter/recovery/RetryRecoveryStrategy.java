package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.HealthStatus;
import com.selfheal.starter.event.SelfHealEvent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.event.SelfHealEventType;

public class RetryRecoveryStrategy implements RecoveryStrategy {

    private final int maxAttempts;
    private final long delay;
    private final RecoveryAction recoveryAction;
    private final SelfHealEventPublisher eventPublisher;

    public RetryRecoveryStrategy(
            int maxAttempts,
            long delay,
            RecoveryAction recoveryAction,
            SelfHealEventPublisher eventPublisher) {

        this.maxAttempts = maxAttempts;
        this.delay = delay;
        this.recoveryAction = recoveryAction;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String getName() {
        return "RETRY";
    }

    @Override
    public boolean recover(HealthCheck healthCheck) {

        String componentName = healthCheck.getName();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            eventPublisher.publish(
                    new SelfHealEvent(
                            SelfHealEventType.RECOVERY_ATTEMPT,
                            componentName,
                            "Recovery attempt "
                                    + attempt
                                    + "/"
                                    + maxAttempts
                    )
            );

            System.out.println(
                    "[SELFHEAL] Recovery attempt "
                            + attempt
                            + "/"
                            + maxAttempts
            );

            boolean actionSuccessful =
                    recoveryAction.execute(healthCheck);

            if (actionSuccessful) {

                HealthStatus status = healthCheck.check();

                if (status == HealthStatus.UP) {

                    System.out.println(
                            "[SELFHEAL] Recovery successful."
                    );

                    return true;
                }
            }

            System.out.println(
                    "[SELFHEAL] Recovery attempt failed."
            );

            if (attempt < maxAttempts) {
                sleep();
            }
        }

        System.out.println(
                "[SELFHEAL] Recovery failed after "
                        + maxAttempts
                        + " attempts."
        );

        return false;
    }

    private void sleep() {

        try {

            Thread.sleep(delay);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    "[SELFHEAL] Recovery interrupted."
            );
        }
    }
}
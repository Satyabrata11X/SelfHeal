package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.HealthCheckResult;
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
    public boolean recover(
            HealthCheck healthCheck,
            RecoveryContext context) {

        String componentName = healthCheck.getName();

        for (int attempt = 1;
             attempt <= maxAttempts;
             attempt++) {

            eventPublisher.publish(
                    new SelfHealEvent(
                            SelfHealEventType.RECOVERY_ATTEMPT,
                            componentName,
                            "Recovery attempt "
                                    + attempt
                                    + "/"
                                    + maxAttempts
                                    + " | failureType="
                                    + context.getFailureType()
                    )
            );

            System.out.println(
                    "[SELFHEAL] Recovery attempt "
                            + attempt
                            + "/"
                            + maxAttempts
            );

            boolean actionSuccessful =
                    recoveryAction.execute(
                            healthCheck,
                            context
                    );

            if (actionSuccessful) {

                HealthCheckResult result =
                        healthCheck.check();

                if (result.isHealthy()) {

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
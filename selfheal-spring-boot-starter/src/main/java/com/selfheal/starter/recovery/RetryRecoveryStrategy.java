package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.event.SelfHealEvent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.event.SelfHealEventType;

public class RetryRecoveryStrategy
        implements RecoveryStrategy {

    private final int maxAttempts;

    private final long initialDelay;

    private final double backoffMultiplier;

    private final long maxDelay;

    private final RecoveryAction recoveryAction;

    private final SelfHealEventPublisher eventPublisher;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RetryRecoveryStrategy(
            int maxAttempts,
            long initialDelay,
            double backoffMultiplier,
            long maxDelay,
            RecoveryAction recoveryAction,
            SelfHealEventPublisher eventPublisher) {

        this.maxAttempts = maxAttempts;
        this.initialDelay = initialDelay;
        this.backoffMultiplier = backoffMultiplier;
        this.maxDelay = maxDelay;
        this.recoveryAction = recoveryAction;
        this.eventPublisher = eventPublisher;
    }


    // =========================================================
    // STRATEGY NAME
    // =========================================================

    @Override
    public String getName() {

        return "RETRY";
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
        // Recovery Attempts
        // ------------------------------------------

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


            // ------------------------------------------
            // Execute Recovery Action
            // ------------------------------------------

            boolean actionSuccessful =
                    recoveryAction.execute(
                            healthCheck,
                            context
                    );


            // ------------------------------------------
            // Verify Recovery
            // ------------------------------------------

            if (actionSuccessful) {

                HealthCheckResult result =
                        healthCheck.check();

                if (result.isHealthy()) {

                    long duration =
                            System.currentTimeMillis()
                                    - startTime;

                    System.out.println(
                            "[SELFHEAL] Recovery successful on attempt "
                                    + attempt
                    );

                    return new RecoveryResult(
                            true,
                            attempt,
                            duration
                    );
                }
            }


            // ------------------------------------------
            // Attempt Failed
            // ------------------------------------------

            System.out.println(
                    "[SELFHEAL] Recovery attempt "
                            + attempt
                            + " failed."
            );


            // ------------------------------------------
            // Maximum Attempts Reached
            // ------------------------------------------

            if (attempt >= maxAttempts) {
                break;
            }


            // ------------------------------------------
            // Exponential Backoff
            // ------------------------------------------

            long delay =
                    calculateDelay(attempt);

            System.out.println(
                    "[SELFHEAL] Waiting "
                            + delay
                            + "ms before next attempt."
            );

            sleep(delay);
        }


        // ------------------------------------------
        // Recovery Failed
        // ------------------------------------------

        long duration =
                System.currentTimeMillis()
                        - startTime;

        System.out.println(
                "[SELFHEAL] Recovery failed after "
                        + maxAttempts
                        + " attempts."
        );

        return new RecoveryResult(
                false,
                maxAttempts,
                duration
        );
    }


    // =========================================================
    // EXPONENTIAL BACKOFF
    // =========================================================

    private long calculateDelay(int attempt) {

        double calculatedDelay =
                initialDelay
                        * Math.pow(
                        backoffMultiplier,
                        attempt - 1
                );

        long delay =
                (long) calculatedDelay;

        return Math.min(
                delay,
                maxDelay
        );
    }


    // =========================================================
    // SLEEP
    // =========================================================

    private void sleep(long delay) {

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
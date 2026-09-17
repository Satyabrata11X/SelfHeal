package com.selfheal.starter.recovery;

import com.selfheal.starter.event.SelfHealEventPublisher;

import java.util.Locale;

public class RecoveryStrategyFactory {

    private final RecoveryAction recoveryAction;
    private final SelfHealEventPublisher eventPublisher;

    public RecoveryStrategyFactory(
            RecoveryAction recoveryAction,
            SelfHealEventPublisher eventPublisher) {

        this.recoveryAction = recoveryAction;
        this.eventPublisher = eventPublisher;
    }

    public RecoveryStrategy create(RecoveryPolicy policy) {

        String strategy =
                policy.getStrategy()
                        .toLowerCase(Locale.ROOT);

        return switch (strategy) {

            case "retry" -> new RetryRecoveryStrategy(
                    policy.getMaxAttempts(),
                    policy.getDelay(),
                    recoveryAction,
                    eventPublisher
            );

            case "immediate" -> new ImmediateRecoveryStrategy(
                    recoveryAction,
                    eventPublisher
            );

            default -> throw new IllegalArgumentException(
                    "Unsupported SelfHeal recovery strategy: "
                            + policy.getStrategy()
            );
        };
    }
}
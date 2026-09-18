package com.selfheal.starter.recovery;

import java.util.concurrent.atomic.AtomicLong;

public class RecoveryCooldown {

    private final long cooldownMillis;

    private final AtomicLong lastRecoveryTime =
            new AtomicLong(0);

    public RecoveryCooldown(long cooldownMillis) {

        if (cooldownMillis < 0) {
            throw new IllegalArgumentException(
                    "Cooldown cannot be negative"
            );
        }

        this.cooldownMillis = cooldownMillis;
    }

    public boolean isInCooldown() {

        long lastRecovery =
                lastRecoveryTime.get();

        if (lastRecovery == 0) {
            return false;
        }

        long elapsed =
                System.currentTimeMillis()
                        - lastRecovery;

        return elapsed < cooldownMillis;
    }

    public long getRemainingCooldown() {

        long lastRecovery =
                lastRecoveryTime.get();

        if (lastRecovery == 0) {
            return 0;
        }

        long elapsed =
                System.currentTimeMillis()
                        - lastRecovery;

        long remaining =
                cooldownMillis - elapsed;

        return Math.max(
                remaining,
                0
        );
    }

    public void startCooldown() {

        lastRecoveryTime.set(
                System.currentTimeMillis()
        );
    }

    public long getCooldownMillis() {
        return cooldownMillis;
    }

    public long getLastRecoveryTime() {
        return lastRecoveryTime.get();
    }
}
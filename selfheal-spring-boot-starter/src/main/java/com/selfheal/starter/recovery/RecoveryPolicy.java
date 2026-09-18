package com.selfheal.starter.recovery;

public class RecoveryPolicy {

    private final String strategy;

    private final int maxAttempts;

    private final long delay;

    private final double backoffMultiplier;

    private final long maxDelay;

    private final long cooldown;

    public RecoveryPolicy(
            String strategy,
            int maxAttempts,
            long delay,
            double backoffMultiplier,
            long maxDelay,
            long cooldown) {

        this.strategy = strategy;
        this.maxAttempts = maxAttempts;
        this.delay = delay;
        this.backoffMultiplier = backoffMultiplier;
        this.maxDelay = maxDelay;
        this.cooldown = cooldown;
    }

    public String getStrategy() {
        return strategy;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public long getDelay() {
        return delay;
    }

    public double getBackoffMultiplier() {
        return backoffMultiplier;
    }

    public long getMaxDelay() {
        return maxDelay;
    }

    public long getCooldown() {
        return cooldown;
    }
}
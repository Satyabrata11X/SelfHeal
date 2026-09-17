package com.selfheal.starter.recovery;

public class RecoveryPolicy {

    private final String strategy;
    private final int maxAttempts;
    private final long delay;

    public RecoveryPolicy(
            String strategy,
            int maxAttempts,
            long delay) {

        this.strategy = strategy;
        this.maxAttempts = maxAttempts;
        this.delay = delay;
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
}
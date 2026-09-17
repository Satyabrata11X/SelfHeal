package com.selfheal.starter.recovery;

public class RecoveryPolicy {

    private final int maxAttempts;
    private final long delay;

    public RecoveryPolicy(
            int maxAttempts,
            long delay) {

        this.maxAttempts = maxAttempts;
        this.delay = delay;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public long getDelay() {
        return delay;
    }
}
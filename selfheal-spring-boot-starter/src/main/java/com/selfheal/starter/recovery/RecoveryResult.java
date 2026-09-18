package com.selfheal.starter.recovery;

public class RecoveryResult {

    private final boolean successful;

    private final int attempts;

    private final long duration;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RecoveryResult(
            boolean successful,
            int attempts,
            long duration) {

        this.successful = successful;
        this.attempts = attempts;
        this.duration = duration;
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public boolean isSuccessful() {

        return successful;
    }

    public int getAttempts() {

        return attempts;
    }

    public long getDuration() {

        return duration;
    }
}
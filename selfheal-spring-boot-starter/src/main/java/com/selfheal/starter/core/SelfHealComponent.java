package com.selfheal.starter.core;

import com.selfheal.starter.failure.FailureType;

import java.util.concurrent.atomic.AtomicBoolean;

public class SelfHealComponent implements HealthCheck {

    private final AtomicBoolean healthy =
            new AtomicBoolean(true);

    private volatile long simulatedLatency = 0;

    private volatile FailureType simulatedFailure = null;

    private volatile int simulatedRecoveryFailures = 0;

    public void setSimulatedRecoveryFailures(
            int failures) {

        if (failures < 0) {

            throw new IllegalArgumentException(
                    "Recovery failures cannot be negative"
            );
        }

        simulatedRecoveryFailures =
                failures;
    }

    public int getSimulatedRecoveryFailures() {
        return simulatedRecoveryFailures;
    }

    public boolean consumeSimulatedRecoveryFailure() {

        if (simulatedRecoveryFailures <= 0) {
            return false;
        }

        simulatedRecoveryFailures--;

        return true;
    }

    @Override
    public String getName() {
        return "selfheal-demo-component";
    }

    @Override
    public HealthCheckResult check() {

        long startTime = System.nanoTime();

        simulateLatency();

        long responseTime =
                (System.nanoTime() - startTime)
                        / 1_000_000;

        FailureType failure =
                simulatedFailure;

        if (failure != null) {

            return HealthCheckResult.down(
                    failure,
                    getFailureMessage(failure),
                    responseTime
            );
        }

        if (!healthy.get()) {

            return HealthCheckResult.down(
                    FailureType.COMPONENT_FAILURE,
                    "Health check reported DOWN",
                    responseTime
            );
        }

        return HealthCheckResult.up(
                responseTime
        );
    }

    private void simulateLatency() {

        if (simulatedLatency <= 0) {
            return;
        }

        try {

            Thread.sleep(
                    simulatedLatency
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }

    private String getFailureMessage(
            FailureType failureType) {

        return switch (failureType) {

            case TIMEOUT ->
                    "Simulated timeout failure";

            case CONNECTION_ERROR ->
                    "Simulated connection failure";

            case DATABASE_FAILURE ->
                    "Simulated database failure";

            case COMPONENT_FAILURE ->
                    "Simulated component failure";

            default ->
                    "Simulated failure: "
                            + failureType;
        };
    }

    // ------------------------------------------
    // Component Failure
    // ------------------------------------------

    public void fail() {
        healthy.set(false);
    }

    public void recover() {
        healthy.set(true);
        simulatedFailure = null;
    }

    public boolean isHealthy() {
        return healthy.get()
                && simulatedFailure == null;
    }

    // ------------------------------------------
    // Latency Simulation
    // ------------------------------------------

    public void setSimulatedLatency(
            long latency) {

        if (latency < 0) {

            throw new IllegalArgumentException(
                    "Latency cannot be negative"
            );
        }

        simulatedLatency = latency;
    }

    public long getSimulatedLatency() {
        return simulatedLatency;
    }

    public void clearSimulatedLatency() {
        simulatedLatency = 0;
    }

    // ------------------------------------------
    // Failure Simulation
    // ------------------------------------------

    public void simulateFailure(
            FailureType failureType) {

        if (failureType == null) {

            throw new IllegalArgumentException(
                    "Failure type cannot be null"
            );
        }

        simulatedFailure =
                failureType;

        healthy.set(false);
    }

    public FailureType getSimulatedFailure() {
        return simulatedFailure;
    }

    public void clearSimulatedFailure() {

        simulatedFailure = null;

        healthy.set(true);
    }
}
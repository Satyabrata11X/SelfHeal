package com.selfheal.starter.core;

import com.selfheal.starter.failure.FailureType;

import java.util.concurrent.atomic.AtomicBoolean;

public class SelfHealComponent implements HealthCheck {

    private final AtomicBoolean healthy =
            new AtomicBoolean(true);

    private volatile long simulatedLatency = 0;

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

        if (healthy.get()) {

            return HealthCheckResult.up(
                    responseTime
            );
        }

        return HealthCheckResult.down(
                FailureType.COMPONENT_FAILURE,
                "Health check reported DOWN",
                responseTime
        );
    }

    private void simulateLatency() {

        if (simulatedLatency <= 0) {
            return;
        }

        try {

            Thread.sleep(simulatedLatency);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }

    public void fail() {
        healthy.set(false);
    }

    public void recover() {
        healthy.set(true);
    }

    public boolean isHealthy() {
        return healthy.get();
    }

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
}
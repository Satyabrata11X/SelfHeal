package com.selfheal.starter.recovery;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CircuitBreaker {

    private final CircuitBreakerConfig config;

    private final AtomicReference<CircuitBreakerState> state =
            new AtomicReference<>(CircuitBreakerState.CLOSED);

    private final AtomicInteger failureCount =
            new AtomicInteger(0);

    private final AtomicInteger halfOpenCalls =
            new AtomicInteger(0);

    private volatile long openedAt = 0L;

    public CircuitBreaker(CircuitBreakerConfig config) {
        if (config == null) {
            throw new IllegalArgumentException(
                    "Circuit breaker config cannot be null"
            );
        }

        this.config = config;
    }

    /**
     * Determines whether a request is currently allowed.
     */
    public boolean allowRequest() {

        CircuitBreakerState currentState = state.get();

        if (currentState == CircuitBreakerState.CLOSED) {
            return true;
        }

        if (currentState == CircuitBreakerState.OPEN) {

            if (isOpenDurationExpired()) {
                transitionToHalfOpen();
            } else {
                return false;
            }
        }

        if (state.get() == CircuitBreakerState.HALF_OPEN) {

            int calls = halfOpenCalls.incrementAndGet();

            if (calls <= config.getHalfOpenMaxCalls()) {
                return true;
            }

            halfOpenCalls.decrementAndGet();

            return false;
        }

        return false;
    }

    /**
     * Records a successful request.
     */
    public void recordSuccess() {

        CircuitBreakerState currentState = state.get();

        if (currentState == CircuitBreakerState.HALF_OPEN) {

            failureCount.set(0);
            halfOpenCalls.set(0);

            state.set(CircuitBreakerState.CLOSED);

            System.out.println(
                    "[SELFHEAL-CIRCUIT] HALF_OPEN -> CLOSED"
            );

            return;
        }

        if (currentState == CircuitBreakerState.CLOSED) {
            failureCount.set(0);
        }
    }

    /**
     * Records a failed request.
     */
    public void recordFailure() {

        CircuitBreakerState currentState = state.get();

        if (currentState == CircuitBreakerState.HALF_OPEN) {

            openCircuit();

            return;
        }

        if (currentState != CircuitBreakerState.CLOSED) {
            return;
        }

        int failures = failureCount.incrementAndGet();

        System.out.println(
                "[SELFHEAL-CIRCUIT] Failure count: " + failures
        );

        if (failures >= config.getFailureThreshold()) {
            openCircuit();
        }
    }

    /**
     * Opens the circuit.
     */
    private void openCircuit() {

        failureCount.set(0);
        halfOpenCalls.set(0);

        openedAt = System.currentTimeMillis();

        state.set(CircuitBreakerState.OPEN);

        System.out.println(
                "[SELFHEAL-CIRCUIT] CLOSED/HALF_OPEN -> OPEN"
        );
    }

    /**
     * Moves the circuit from OPEN to HALF_OPEN
     * after the configured duration.
     */
    private void transitionToHalfOpen() {

        if (state.compareAndSet(
                CircuitBreakerState.OPEN,
                CircuitBreakerState.HALF_OPEN
        )) {

            halfOpenCalls.set(0);

            System.out.println(
                    "[SELFHEAL-CIRCUIT] OPEN -> HALF_OPEN"
            );
        }
    }

    /**
     * Checks whether the configured open duration has expired.
     */
    private boolean isOpenDurationExpired() {

        return System.currentTimeMillis() - openedAt
                >= config.getOpenDuration();
    }

    public CircuitBreakerState getState() {
        return state.get();
    }

    public int getFailureCount() {
        return failureCount.get();
    }

    public int getHalfOpenCalls() {
        return halfOpenCalls.get();
    }

    public long getOpenedAt() {
        return openedAt;
    }

    public CircuitBreakerConfig getConfig() {
        return config;
    }

    /**
     * Manually resets the circuit.
     */
    public void reset() {

        failureCount.set(0);
        halfOpenCalls.set(0);
        openedAt = 0L;

        state.set(CircuitBreakerState.CLOSED);

        System.out.println(
                "[SELFHEAL-CIRCUIT] Circuit manually reset -> CLOSED"
        );
    }
}
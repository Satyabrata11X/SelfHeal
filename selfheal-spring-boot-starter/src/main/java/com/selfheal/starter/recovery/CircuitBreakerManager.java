package com.selfheal.starter.recovery;

import com.selfheal.starter.config.SelfHealProperties;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CircuitBreakerManager {

    private final SelfHealProperties.CircuitBreaker properties;

    private final Map<String, CircuitBreaker> circuitBreakers =
            new ConcurrentHashMap<>();

    public CircuitBreakerManager(
            SelfHealProperties.CircuitBreaker properties) {

        if (properties == null) {
            throw new IllegalArgumentException(
                    "Circuit breaker properties cannot be null"
            );
        }

        this.properties = properties;
    }

    /**
     * Returns the circuit breaker for a component.
     *
     * A new circuit breaker is created automatically
     * when the component is seen for the first time.
     */
    public CircuitBreaker getOrCreate(String componentName) {

        if (componentName == null || componentName.isBlank()) {
            throw new IllegalArgumentException(
                    "Component name cannot be empty"
            );
        }

        return circuitBreakers.computeIfAbsent(
                componentName,
                name -> createCircuitBreaker()
        );
    }

    /**
     * Checks whether a component is currently allowed
     * to proceed.
     */
    public boolean allowRequest(String componentName) {

        if (!properties.isEnabled()) {
            return true;
        }

        return getOrCreate(componentName).allowRequest();
    }

    /**
     * Records a successful operation for a component.
     */
    public void recordSuccess(String componentName) {

        if (!properties.isEnabled()) {
            return;
        }

        getOrCreate(componentName).recordSuccess();
    }

    /**
     * Records a failed operation for a component.
     */
    public void recordFailure(String componentName) {

        if (!properties.isEnabled()) {
            return;
        }

        getOrCreate(componentName).recordFailure();
    }

    /**
     * Returns the current state of a component's circuit.
     */
    public CircuitBreakerState getState(String componentName) {

        if (!properties.isEnabled()) {
            return CircuitBreakerState.CLOSED;
        }

        return getOrCreate(componentName).getState();
    }

    /**
     * Resets a component's circuit breaker.
     */
    public void reset(String componentName) {

        getOrCreate(componentName).reset();
    }

    /**
     * Returns all registered circuit breakers.
     *
     * The returned map cannot be modified by callers.
     */
    public Map<String, CircuitBreaker> getAll() {

        return Collections.unmodifiableMap(circuitBreakers);
    }

    /**
     * Returns the number of registered circuit breakers.
     */
    public int size() {

        return circuitBreakers.size();
    }

    /**
     * Removes a component's circuit breaker.
     */
    public void remove(String componentName) {

        if (componentName == null || componentName.isBlank()) {
            return;
        }

        circuitBreakers.remove(componentName);
    }

    /**
     * Removes all circuit breakers.
     */
    public void clear() {

        circuitBreakers.clear();
    }

    private CircuitBreaker createCircuitBreaker() {

        CircuitBreakerConfig config =
                new CircuitBreakerConfig(
                        properties.getFailureThreshold(),
                        properties.getOpenDuration(),
                        properties.getHalfOpenMaxCalls()
                );

        return new CircuitBreaker(config);
    }
}
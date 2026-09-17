package com.selfheal.starter.core;

import java.util.concurrent.atomic.AtomicBoolean;

public class SelfHealComponent implements HealthCheck {

    private final AtomicBoolean healthy = new AtomicBoolean(true);

    @Override
    public String getName() {
        return "selfheal-demo-component";
    }

    @Override
    public HealthStatus check() {

        return healthy.get()
                ? HealthStatus.UP
                : HealthStatus.DOWN;
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
}
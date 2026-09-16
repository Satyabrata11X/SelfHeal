package com.selfheal.starter.core;

import java.util.concurrent.atomic.AtomicBoolean;

public class SelfHealComponent {

    private final AtomicBoolean healthy = new AtomicBoolean(true);

    public boolean isHealthy() {
        return healthy.get();
    }

    public void fail() {
        healthy.set(false);
    }

    public void recover() {
        healthy.set(true);
    }
}
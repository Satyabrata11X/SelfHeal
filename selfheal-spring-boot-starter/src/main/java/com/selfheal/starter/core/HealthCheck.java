package com.selfheal.starter.core;

public interface HealthCheck {

    String getName();

    HealthStatus check();
}
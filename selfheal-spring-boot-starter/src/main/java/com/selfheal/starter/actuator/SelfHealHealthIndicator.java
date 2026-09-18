package com.selfheal.starter.actuator;

import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.core.SelfHealComponent;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

public class SelfHealHealthIndicator
        implements HealthIndicator {

    private final SelfHealComponent component;

    public SelfHealHealthIndicator(
            SelfHealComponent component) {

        this.component = component;
    }

    @Override
    public Health health() {

        HealthCheckResult result =
                component.check();

        if (result.isHealthy()) {

            return Health.up()
                    .withDetail(
                            "component",
                            component.getName()
                    )
                    .withDetail(
                            "responseTime",
                            result.getResponseTime() + "ms"
                    )
                    .withDetail(
                            "message",
                            result.getMessage()
                    )
                    .build();
        }

        return Health.down()
                .withDetail(
                        "component",
                        component.getName()
                )
                .withDetail(
                        "failureType",
                        result.getFailureType()
                )
                .withDetail(
                        "responseTime",
                        result.getResponseTime() + "ms"
                )
                .withDetail(
                        "message",
                        result.getMessage()
                )
                .build();
    }
}
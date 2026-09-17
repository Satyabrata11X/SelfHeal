package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;

public interface RecoveryStrategy {

    String getName();

    boolean recover(
            HealthCheck healthCheck,
            RecoveryContext context
    );
}
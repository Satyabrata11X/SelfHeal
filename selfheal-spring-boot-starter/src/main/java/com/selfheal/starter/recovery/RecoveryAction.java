package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;

public interface RecoveryAction {

    String getName();

    boolean execute(
            HealthCheck healthCheck,
            RecoveryContext context
    );
}
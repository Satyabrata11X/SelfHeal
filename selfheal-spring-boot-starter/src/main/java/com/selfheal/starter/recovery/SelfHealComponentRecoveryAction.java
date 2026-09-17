package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.SelfHealComponent;

public class SelfHealComponentRecoveryAction implements RecoveryAction {

    @Override
    public String getName() {
        return "COMPONENT_RECOVERY";
    }

    @Override
    public boolean execute(HealthCheck healthCheck) {

        if (healthCheck instanceof SelfHealComponent component) {

            System.out.println(
                    "[SELFHEAL] Executing component recovery..."
            );

            component.recover();

            return component.isHealthy();
        }

        System.out.println(
                "[SELFHEAL] No recovery action available for: "
                        + healthCheck.getName()
        );

        return false;
    }
}
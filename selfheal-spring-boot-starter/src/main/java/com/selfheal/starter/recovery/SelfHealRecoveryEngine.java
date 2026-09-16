package com.selfheal.starter.recovery;

import com.selfheal.starter.core.SelfHealComponent;

public class SelfHealRecoveryEngine {

    public void recover(SelfHealComponent component) {

        System.out.println("[SELFHEAL] Failure detected.");
        System.out.println("[SELFHEAL] Starting recovery...");

        component.recover();

        System.out.println("[SELFHEAL] Recovery completed.");
        System.out.println("[SELFHEAL] Component is healthy: "
                + component.isHealthy());
    }
}
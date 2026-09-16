package com.selfheal.starter;

import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.monitoring.SelfHealMonitor;
import com.selfheal.starter.recovery.SelfHealRecoveryEngine;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class SelfHealAutoConfiguration {

    @Bean
    public SelfHealComponent selfHealComponent() {
        return new SelfHealComponent();
    }

    @Bean
    public SelfHealRecoveryEngine selfHealRecoveryEngine() {
        return new SelfHealRecoveryEngine();
    }

    @Bean
    public SelfHealMonitor selfHealMonitor(
            SelfHealComponent component,
            SelfHealRecoveryEngine recoveryEngine) {

        SelfHealMonitor monitor =
                new SelfHealMonitor(component, recoveryEngine);

        monitor.start();

        return monitor;
    }

    @Bean
    public SelfHealInitializer selfHealInitializer() {
        return new SelfHealInitializer();
    }
}
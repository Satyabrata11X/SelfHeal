package com.selfheal.starter;

import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.monitoring.SelfHealMonitor;
import com.selfheal.starter.recovery.RecoveryAction;
import com.selfheal.starter.recovery.RecoveryStrategy;
import com.selfheal.starter.recovery.RetryRecoveryStrategy;
import com.selfheal.starter.recovery.SelfHealComponentRecoveryAction;
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
    public RecoveryAction recoveryAction() {
        return new SelfHealComponentRecoveryAction();
    }

    @Bean
    public RecoveryStrategy recoveryStrategy(
            RecoveryAction recoveryAction) {

        return new RetryRecoveryStrategy(
                3,
                recoveryAction
        );
    }

    @Bean
    public SelfHealRecoveryEngine selfHealRecoveryEngine(
            RecoveryStrategy recoveryStrategy) {

        return new SelfHealRecoveryEngine(recoveryStrategy);
    }

    @Bean
    public SelfHealMonitor selfHealMonitor(
            SelfHealComponent component,
            SelfHealRecoveryEngine recoveryEngine) {

        SelfHealMonitor monitor =
                new SelfHealMonitor(
                        component,
                        recoveryEngine
                );

        monitor.start();

        return monitor;
    }

    @Bean
    public SelfHealInitializer selfHealInitializer() {
        return new SelfHealInitializer();
    }
}
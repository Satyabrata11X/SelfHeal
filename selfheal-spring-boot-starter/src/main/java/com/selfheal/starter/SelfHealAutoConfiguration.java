package com.selfheal.starter;

import com.selfheal.starter.config.SelfHealProperties;
import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.monitoring.SelfHealMonitor;
import com.selfheal.starter.recovery.RecoveryAction;
import com.selfheal.starter.recovery.RecoveryPolicy;
import com.selfheal.starter.recovery.RecoveryStrategy;
import com.selfheal.starter.recovery.RetryRecoveryStrategy;
import com.selfheal.starter.recovery.SelfHealComponentRecoveryAction;
import com.selfheal.starter.recovery.SelfHealRecoveryEngine;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(SelfHealProperties.class)
public class SelfHealAutoConfiguration {

    @Bean
    public SelfHealComponent selfHealComponent() {
        return new SelfHealComponent();
    }

    @Bean
    public SelfHealEventPublisher selfHealEventPublisher() {
        return new SelfHealEventPublisher();
    }

    @Bean
    public RecoveryAction recoveryAction() {
        return new SelfHealComponentRecoveryAction();
    }

    @Bean
    public RecoveryPolicy recoveryPolicy(
            SelfHealProperties properties) {

        return new RecoveryPolicy(
                properties.getRecovery().getMaxAttempts(),
                properties.getRecovery().getDelay()
        );
    }

    @Bean
    public RecoveryStrategy recoveryStrategy(
            RecoveryPolicy recoveryPolicy,
            RecoveryAction recoveryAction,
            SelfHealEventPublisher eventPublisher) {

        System.out.println(
                "[SELFHEAL] Recovery Policy: "
                        + "maxAttempts="
                        + recoveryPolicy.getMaxAttempts()
                        + ", delay="
                        + recoveryPolicy.getDelay()
        );

        return new RetryRecoveryStrategy(
                recoveryPolicy.getMaxAttempts(),
                recoveryPolicy.getDelay(),
                recoveryAction,
                eventPublisher
        );
    }

    @Bean
    public SelfHealRecoveryEngine selfHealRecoveryEngine(
            RecoveryStrategy recoveryStrategy,
            SelfHealEventPublisher eventPublisher) {

        return new SelfHealRecoveryEngine(
                recoveryStrategy,
                eventPublisher
        );
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "selfheal",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public SelfHealMonitor selfHealMonitor(
            SelfHealComponent component,
            SelfHealRecoveryEngine recoveryEngine,
            SelfHealEventPublisher eventPublisher) {

        SelfHealMonitor monitor =
                new SelfHealMonitor(
                        component,
                        recoveryEngine,
                        eventPublisher
                );

        monitor.start();

        return monitor;
    }

    @Bean
    public SelfHealInitializer selfHealInitializer() {
        return new SelfHealInitializer();
    }
}
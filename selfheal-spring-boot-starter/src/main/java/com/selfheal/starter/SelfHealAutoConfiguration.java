package com.selfheal.starter;

import com.selfheal.starter.config.SelfHealProperties;
import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.failure.FailureClassifier;
import com.selfheal.starter.monitoring.SelfHealMonitor;
import com.selfheal.starter.recovery.RecoveryAction;
import com.selfheal.starter.recovery.RecoveryPolicy;
import com.selfheal.starter.recovery.RecoveryStrategy;
import com.selfheal.starter.recovery.RecoveryStrategyFactory;
import com.selfheal.starter.recovery.SelfHealComponentRecoveryAction;
import com.selfheal.starter.recovery.SelfHealRecoveryEngine;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(SelfHealProperties.class)
public class SelfHealAutoConfiguration {

    // --------------------------------------------------
    // SelfHeal Component
    // --------------------------------------------------

    @Bean
    public SelfHealComponent selfHealComponent() {
        return new SelfHealComponent();
    }

    // --------------------------------------------------
    // Event Publisher
    // --------------------------------------------------

    @Bean
    public SelfHealEventPublisher selfHealEventPublisher() {
        return new SelfHealEventPublisher();
    }

    // --------------------------------------------------
    // Failure Classifier
    // --------------------------------------------------

    @Bean
    public FailureClassifier failureClassifier() {
        return new FailureClassifier();
    }

    // --------------------------------------------------
    // Recovery Action
    // --------------------------------------------------

    @Bean
    public RecoveryAction recoveryAction() {
        return new SelfHealComponentRecoveryAction();
    }

    // --------------------------------------------------
    // Recovery Policy
    // --------------------------------------------------

    @Bean
    public RecoveryPolicy recoveryPolicy(
            SelfHealProperties properties) {

        return new RecoveryPolicy(
                properties.getRecovery().getStrategy(),
                properties.getRecovery().getMaxAttempts(),
                properties.getRecovery().getDelay()
        );
    }

    // --------------------------------------------------
    // Recovery Strategy Factory
    // --------------------------------------------------

    @Bean
    public RecoveryStrategyFactory recoveryStrategyFactory(
            RecoveryAction recoveryAction,
            SelfHealEventPublisher eventPublisher) {

        return new RecoveryStrategyFactory(
                recoveryAction,
                eventPublisher
        );
    }

    // --------------------------------------------------
    // Recovery Strategy
    // --------------------------------------------------

    @Bean
    public RecoveryStrategy recoveryStrategy(
            RecoveryPolicy recoveryPolicy,
            RecoveryStrategyFactory strategyFactory) {

        System.out.println(
                "[SELFHEAL] Recovery Policy:"
                        + " strategy="
                        + recoveryPolicy.getStrategy()
                        + ", maxAttempts="
                        + recoveryPolicy.getMaxAttempts()
                        + ", delay="
                        + recoveryPolicy.getDelay()
        );

        return strategyFactory.create(recoveryPolicy);
    }

    // --------------------------------------------------
    // Recovery Engine
    // --------------------------------------------------

    @Bean
    public SelfHealRecoveryEngine selfHealRecoveryEngine(
            RecoveryStrategy recoveryStrategy,
            SelfHealEventPublisher eventPublisher) {

        return new SelfHealRecoveryEngine(
                recoveryStrategy,
                eventPublisher
        );
    }

    // --------------------------------------------------
    // SelfHeal Monitor
    // --------------------------------------------------

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
            SelfHealEventPublisher eventPublisher,
            FailureClassifier failureClassifier,
            SelfHealProperties properties) {

        SelfHealMonitor monitor =
                new SelfHealMonitor(
                        component,
                        recoveryEngine,
                        eventPublisher,
                        failureClassifier,
                        properties
                );

        monitor.start();

        return monitor;
    }

    // --------------------------------------------------
    // Initializer
    // --------------------------------------------------

    @Bean
    public SelfHealInitializer selfHealInitializer() {
        return new SelfHealInitializer();
    }
}
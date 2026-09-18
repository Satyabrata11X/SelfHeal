package com.selfheal.starter;

import com.selfheal.starter.config.SelfHealProperties;
import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.failure.FailureClassifier;
import com.selfheal.starter.history.RecoveryHistory;
import com.selfheal.starter.monitoring.SelfHealMonitor;
import com.selfheal.starter.recovery.RecoveryAction;
import com.selfheal.starter.recovery.RecoveryCooldown;
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

    // =========================================================
    // SELFHEAL COMPONENT
    // =========================================================

    @Bean
    public SelfHealComponent selfHealComponent() {

        return new SelfHealComponent();
    }


    // =========================================================
    // EVENT PUBLISHER
    // =========================================================

    @Bean
    public SelfHealEventPublisher selfHealEventPublisher() {

        return new SelfHealEventPublisher();
    }


    // =========================================================
    // FAILURE CLASSIFIER
    // =========================================================

    @Bean
    public FailureClassifier failureClassifier() {

        return new FailureClassifier();
    }


    // =========================================================
    // RECOVERY ACTION
    // =========================================================

    @Bean
    public RecoveryAction recoveryAction() {

        return new SelfHealComponentRecoveryAction();
    }


    // =========================================================
    // RECOVERY POLICY
    // =========================================================

    @Bean
    public RecoveryPolicy recoveryPolicy(
            SelfHealProperties properties) {

        return new RecoveryPolicy(
                properties.getRecovery().getStrategy(),
                properties.getRecovery().getMaxAttempts(),
                properties.getRecovery().getDelay(),
                properties.getRecovery().getBackoffMultiplier(),
                properties.getRecovery().getMaxDelay(),
                properties.getRecovery().getCooldown()
        );
    }


    // =========================================================
    // RECOVERY STRATEGY FACTORY
    // =========================================================

    @Bean
    public RecoveryStrategyFactory recoveryStrategyFactory(
            RecoveryAction recoveryAction,
            SelfHealEventPublisher eventPublisher) {

        return new RecoveryStrategyFactory(
                recoveryAction,
                eventPublisher
        );
    }


    // =========================================================
    // RECOVERY STRATEGY
    // =========================================================

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
                        + "ms"
                        + ", backoffMultiplier="
                        + recoveryPolicy.getBackoffMultiplier()
                        + ", maxDelay="
                        + recoveryPolicy.getMaxDelay()
                        + "ms"
                        + ", cooldown="
                        + recoveryPolicy.getCooldown()
                        + "ms"
        );

        return strategyFactory.create(
                recoveryPolicy
        );
    }


    // =========================================================
    // RECOVERY ENGINE
    // =========================================================

    @Bean
    public SelfHealRecoveryEngine selfHealRecoveryEngine(
            RecoveryStrategy recoveryStrategy,
            SelfHealEventPublisher eventPublisher) {

        return new SelfHealRecoveryEngine(
                recoveryStrategy,
                eventPublisher
        );
    }


    // =========================================================
    // RECOVERY COOLDOWN
    // =========================================================

    @Bean
    public RecoveryCooldown recoveryCooldown(
            SelfHealProperties properties) {

        return new RecoveryCooldown(
                properties.getRecovery().getCooldown()
        );
    }


    // =========================================================
    // RECOVERY HISTORY
    // =========================================================

    @Bean
    public RecoveryHistory recoveryHistory() {

        return new RecoveryHistory();
    }


    // =========================================================
    // SELFHEAL MONITOR
    // =========================================================

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
            SelfHealProperties properties,
            RecoveryCooldown recoveryCooldown,
            RecoveryHistory recoveryHistory) {

        SelfHealMonitor monitor =
                new SelfHealMonitor(
                        component,
                        recoveryEngine,
                        eventPublisher,
                        failureClassifier,
                        properties,
                        recoveryCooldown,
                        recoveryHistory
                );

        monitor.start();

        return monitor;
    }


    // =========================================================
    // INITIALIZER
    // =========================================================

    @Bean
    public SelfHealInitializer selfHealInitializer() {

        return new SelfHealInitializer();
    }
}
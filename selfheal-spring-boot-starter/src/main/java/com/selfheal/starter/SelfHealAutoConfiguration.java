package com.selfheal.starter;

import com.selfheal.starter.actuator.SelfHealHealthIndicator;
import com.selfheal.starter.actuator.SelfHealInfoContributor;
import com.selfheal.starter.config.SelfHealProperties;
import com.selfheal.starter.core.SelfHealComponent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.failure.FailureClassifier;
import com.selfheal.starter.history.RecoveryHistory;
import com.selfheal.starter.incident.FailureContextFactory;
import com.selfheal.starter.incident.FailureContextService;
import com.selfheal.starter.incident.FailureIncidentRecorder;
import com.selfheal.starter.incident.InMemoryFailureIncidentRecorder;
import com.selfheal.starter.incident.SelfHealExceptionInterceptor;
import com.selfheal.starter.incident.SelfHealIncidentController;
import com.selfheal.starter.incident.SelfHealWebMvcConfiguration;
import com.selfheal.starter.incident.StackTraceLocationExtractor;
import com.selfheal.starter.management.SelfHealManagementController;
import com.selfheal.starter.management.SelfHealManagementService;
import com.selfheal.starter.metrics.SelfHealMetrics;
import com.selfheal.starter.metrics.SelfHealMetricsBinder;
import com.selfheal.starter.monitoring.SelfHealMonitor;
import com.selfheal.starter.recovery.RecoveryAction;
import com.selfheal.starter.recovery.RecoveryCooldown;
import com.selfheal.starter.recovery.RecoveryPolicy;
import com.selfheal.starter.recovery.RecoveryStrategy;
import com.selfheal.starter.recovery.RecoveryStrategyFactory;
import com.selfheal.starter.recovery.SelfHealComponentRecoveryAction;
import com.selfheal.starter.recovery.SelfHealRecoveryEngine;

import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;


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
    // SELFHEAL METRICS
    // =========================================================

    @Bean
    public SelfHealMetrics selfHealMetrics() {
        return new SelfHealMetrics();
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
            RecoveryHistory recoveryHistory,
            SelfHealMetrics metrics) {

        SelfHealMonitor monitor =
                new SelfHealMonitor(
                        component,
                        recoveryEngine,
                        eventPublisher,
                        failureClassifier,
                        properties,
                        recoveryCooldown,
                        recoveryHistory,
                        metrics
                );

        monitor.start();

        return monitor;
    }


    // =========================================================
    // MANAGEMENT SERVICE
    // =========================================================

    @Bean
    @ConditionalOnProperty(
            prefix = "selfheal",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public SelfHealManagementService selfHealManagementService(
            SelfHealComponent component,
            SelfHealMonitor monitor,
            SelfHealMetrics metrics) {

        return new SelfHealManagementService(
                component,
                monitor,
                metrics
        );
    }


    // =========================================================
    // MANAGEMENT CONTROLLER
    // =========================================================

    @Bean
    @ConditionalOnProperty(
            prefix = "selfheal",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public SelfHealManagementController selfHealManagementController(
            SelfHealComponent component,
            SelfHealMonitor monitor,
            SelfHealMetrics metrics,
            RecoveryHistory recoveryHistory) {

        return new SelfHealManagementController(
                component,
                monitor,
                metrics,
                recoveryHistory
        );
    }


    // =========================================================
    // ACTUATOR HEALTH INDICATOR
    // =========================================================

    @Bean
    @ConditionalOnProperty(
            prefix = "selfheal",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public SelfHealHealthIndicator selfHealHealthIndicator(
            SelfHealComponent component) {

        return new SelfHealHealthIndicator(
                component
        );
    }


    // =========================================================
    // ACTUATOR INFO CONTRIBUTOR
    // =========================================================

    @Bean
    @ConditionalOnProperty(
            prefix = "selfheal",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public SelfHealInfoContributor selfHealInfoContributor(
            SelfHealManagementService managementService) {

        return new SelfHealInfoContributor(
                managementService
        );
    }


    // =========================================================
    // MICROMETER METRICS
    // =========================================================

    @Bean
    @ConditionalOnProperty(
            prefix = "selfheal",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public SelfHealMetricsBinder selfHealMetricsBinder(
            SelfHealMetrics metrics,
            MeterRegistry meterRegistry) {

        return new SelfHealMetricsBinder(
                metrics,
                meterRegistry
        );
    }


    // =========================================================
    // FAILURE CONTEXT
    // =========================================================

    @Bean
    public StackTraceLocationExtractor stackTraceLocationExtractor() {
        return new StackTraceLocationExtractor();
    }


    @Bean
    public FailureContextFactory failureContextFactory(
            StackTraceLocationExtractor locationExtractor) {

        return new FailureContextFactory(
                locationExtractor
        );
    }


    @Bean
    public FailureContextService failureContextService(
            FailureContextFactory contextFactory) {

        return new FailureContextService(
                contextFactory
        );
    }


    // =========================================================
    // FAILURE INCIDENT RECORDER
    // =========================================================

    @Bean
    public InMemoryFailureIncidentRecorder failureIncidentRecorder() {
        return new InMemoryFailureIncidentRecorder();
    }


    // =========================================================
    // EXCEPTION INTERCEPTION
    // =========================================================
    @Bean
    public SelfHealExceptionInterceptor selfHealExceptionInterceptor(
            FailureContextService contextService,
            FailureIncidentRecorder incidentRecorder,
            Environment environment) {

        return new SelfHealExceptionInterceptor(
                contextService,
                incidentRecorder,
                environment
        );
    }


    // =========================================================
    // SPRING MVC CONFIGURATION
    // =========================================================

    @Bean
    public SelfHealWebMvcConfiguration selfHealWebMvcConfiguration(
            SelfHealExceptionInterceptor interceptor) {

        return new SelfHealWebMvcConfiguration(
                interceptor
        );
    }


    // =========================================================
    // INCIDENT CONTROLLER
    // =========================================================

    @Bean
    public SelfHealIncidentController selfHealIncidentController(
            InMemoryFailureIncidentRecorder recorder) {

        return new SelfHealIncidentController(
                recorder
        );
    }


    // =========================================================
    // INITIALIZER
    // =========================================================

    @Bean
    public SelfHealInitializer selfHealInitializer() {
        return new SelfHealInitializer();
    }
}
package com.selfheal.starter.recovery;

import com.selfheal.starter.config.SelfHealProperties;
import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.failure.FailureType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelfHealRecoveryEngineEscalationTest {

    @Test
    void shouldEscalateWhenRecoveryFails() {

        SelfHealEventPublisher eventPublisher =
                new SelfHealEventPublisher();

        SelfHealProperties properties =
                new SelfHealProperties();

        CircuitBreakerManager circuitBreakerManager =
                new CircuitBreakerManager(
                        properties.getCircuitBreaker()
                );

        RecoveryEscalationHandler escalationHandler =
                new RecoveryEscalationHandler(eventPublisher);

        RecoveryAction failingAction = new RecoveryAction() {

            @Override
            public String getName() {
                return "FAILING_ACTION";
            }

            @Override
            public boolean execute(
                    HealthCheck healthCheck,
                    RecoveryContext context) {

                return false;
            }
        };

        RecoveryStrategy strategy =
                new RetryRecoveryStrategy(
                        3,
                        0,
                        1.0,
                        0,
                        failingAction,
                        eventPublisher
                );

        SelfHealRecoveryEngine engine =
                new SelfHealRecoveryEngine(
                        strategy,
                        eventPublisher,
                        circuitBreakerManager,
                        escalationHandler
                );

        HealthCheck healthCheck = new HealthCheck() {

            @Override
            public String getName() {
                return "test-component";
            }

            @Override
            public HealthCheckResult check() {

                return HealthCheckResult.down(
                        FailureType.COMPONENT_FAILURE,
                        "Simulated failure",
                        10
                );
            }
        };

        RecoveryContext context =
                new RecoveryContext(
                        healthCheck,
                        healthCheck.check(),
                        FailureType.COMPONENT_FAILURE,
                        "Simulated failure"
                );

        RecoveryResult result =
                engine.recover(
                        healthCheck,
                        context
                );

        assertFalse(result.isSuccessful());

        assertEquals(
                3,
                result.getAttempts()
        );

        assertEquals(
                1,
                escalationHandler.getEscalationCount()
        );

        RecoveryEscalation escalation =
                escalationHandler
                        .getEscalations()
                        .get(0);

        assertEquals(
                "test-component",
                escalation.getComponentName()
        );

        assertEquals(
                FailureType.COMPONENT_FAILURE,
                escalation.getFailureType()
        );

        assertEquals(
                "RETRY",
                escalation.getRecoveryStrategy()
        );

        assertEquals(
                3,
                escalation.getAttempts()
        );
    }
}
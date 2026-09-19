package com.selfheal.starter.recovery;

import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.failure.FailureType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecoveryEscalationHandlerTest {

    @Test
    void shouldCreateEscalationWhenRecoveryFails() {

        SelfHealEventPublisher eventPublisher =
                new SelfHealEventPublisher();

        RecoveryEscalationHandler handler =
                new RecoveryEscalationHandler(eventPublisher);

        RecoveryContext context =
                new RecoveryContext(
                        new TestHealthCheck(),
                        HealthCheckResult.down(
                                FailureType.COMPONENT_FAILURE,
                                "Simulated failure",
                                10
                        ),
                        FailureType.COMPONENT_FAILURE,
                        "Simulated failure"
                );

        RecoveryResult result =
                new RecoveryResult(false, 3, 150);

        RecoveryEscalation escalation =
                handler.escalate(
                        "test-component",
                        context,
                        result,
                        "RETRY"
                );

        assertNotNull(escalation);
        assertEquals("test-component", escalation.getComponentName());
        assertEquals(
                FailureType.COMPONENT_FAILURE,
                escalation.getFailureType()
        );
        assertEquals("RETRY", escalation.getRecoveryStrategy());
        assertEquals(3, escalation.getAttempts());
        assertEquals(150, escalation.getDuration());

        assertEquals(1, handler.getEscalationCount());
    }

    @Test
    void shouldStoreMultipleEscalations() {

        SelfHealEventPublisher eventPublisher =
                new SelfHealEventPublisher();

        RecoveryEscalationHandler handler =
                new RecoveryEscalationHandler(eventPublisher);

        RecoveryContext context =
                new RecoveryContext(
                        new TestHealthCheck(),
                        HealthCheckResult.down(
                                FailureType.TIMEOUT,
                                "Timeout",
                                100
                        ),
                        FailureType.TIMEOUT,
                        "Timeout"
                );

        RecoveryResult result =
                new RecoveryResult(false, 3, 200);

        handler.escalate(
                "component-1",
                context,
                result,
                "RETRY"
        );

        handler.escalate(
                "component-2",
                context,
                result,
                "RETRY"
        );

        assertEquals(2, handler.getEscalationCount());

        List<RecoveryEscalation> escalations =
                handler.getEscalations();

        assertEquals(2, escalations.size());
        assertEquals(
                "component-1",
                escalations.get(0).getComponentName()
        );
        assertEquals(
                "component-2",
                escalations.get(1).getComponentName()
        );
    }

    @Test
    void shouldClearEscalations() {

        SelfHealEventPublisher eventPublisher =
                new SelfHealEventPublisher();

        RecoveryEscalationHandler handler =
                new RecoveryEscalationHandler(eventPublisher);

        RecoveryContext context =
                new RecoveryContext(
                        new TestHealthCheck(),
                        HealthCheckResult.down(
                                FailureType.UNKNOWN,
                                "Failure",
                                10
                        ),
                        FailureType.UNKNOWN,
                        "Failure"
                );

        RecoveryResult result =
                new RecoveryResult(false, 3, 100);

        handler.escalate(
                "test-component",
                context,
                result,
                "RETRY"
        );

        assertEquals(1, handler.getEscalationCount());

        handler.clear();

        assertEquals(0, handler.getEscalationCount());
        assertTrue(handler.getEscalations().isEmpty());
    }

    private static class TestHealthCheck
            implements com.selfheal.starter.core.HealthCheck {

        @Override
        public String getName() {
            return "test-component";
        }

        @Override
        public HealthCheckResult check() {
            return HealthCheckResult.up(10);
        }
    }
}
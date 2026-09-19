package com.selfheal.starter.event;

import com.selfheal.starter.alert.AlertManager;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SelfHealEventPublisherTest {

    @Test
    void shouldRegisterAndNotifyListener() {

        AlertManager alertManager =
                new AlertManager();

        SelfHealEventPublisher publisher =
                new SelfHealEventPublisher(
                        alertManager
                );

        AtomicReference<SelfHealEvent> received =
                new AtomicReference<>();

        SelfHealEventListener listener =
                received::set;

        publisher.registerListener(listener);

        SelfHealEvent event =
                new SelfHealEvent(
                        SelfHealEventType.FAILURE_DETECTED,
                        "test-component",
                        "Test failure"
                );

        publisher.publish(event);

        assertEquals(
                1,
                publisher.getListenerCount()
        );

        assertSame(
                event,
                received.get()
        );
    }

    @Test
    void shouldNotifyMultipleListeners() {

        AlertManager alertManager =
                new AlertManager();

        SelfHealEventPublisher publisher =
                new SelfHealEventPublisher(
                        alertManager
                );

        AtomicInteger counter =
                new AtomicInteger();

        publisher.registerListener(
                event -> counter.incrementAndGet()
        );

        publisher.registerListener(
                event -> counter.incrementAndGet()
        );

        SelfHealEvent event =
                new SelfHealEvent(
                        SelfHealEventType.RECOVERY_SUCCESS,
                        "test-component",
                        "Recovered"
                );

        publisher.publish(event);

        assertEquals(
                2,
                publisher.getListenerCount()
        );

        assertEquals(
                2,
                counter.get()
        );
    }

    @Test
    void shouldUnregisterListener() {

        AlertManager alertManager =
                new AlertManager();

        SelfHealEventPublisher publisher =
                new SelfHealEventPublisher(
                        alertManager
                );

        AtomicInteger counter =
                new AtomicInteger();

        SelfHealEventListener listener =
                event -> counter.incrementAndGet();

        publisher.registerListener(listener);

        assertEquals(
                1,
                publisher.getListenerCount()
        );

        publisher.unregisterListener(listener);

        assertEquals(
                0,
                publisher.getListenerCount()
        );

        publisher.publish(
                new SelfHealEvent(
                        SelfHealEventType.FAILURE_DETECTED,
                        "test-component",
                        "Failure"
                )
        );

        assertEquals(
                0,
                counter.get()
        );
    }

    @Test
    void shouldContinueWhenListenerFails() {

        AlertManager alertManager =
                new AlertManager();

        SelfHealEventPublisher publisher =
                new SelfHealEventPublisher(
                        alertManager
                );

        AtomicInteger counter =
                new AtomicInteger();

        publisher.registerListener(
                event -> {
                    throw new RuntimeException(
                            "Listener failure"
                    );
                }
        );

        publisher.registerListener(
                event -> counter.incrementAndGet()
        );

        publisher.publish(
                new SelfHealEvent(
                        SelfHealEventType.RECOVERY_FAILED,
                        "test-component",
                        "Recovery failed"
                )
        );

        assertEquals(
                1,
                counter.get()
        );
    }

    @Test
    void shouldRejectNullListener() {

        AlertManager alertManager =
                new AlertManager();

        SelfHealEventPublisher publisher =
                new SelfHealEventPublisher(
                        alertManager
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> publisher.registerListener(null)
        );
    }

    @Test
    void shouldIgnoreNullEvent() {

        AlertManager alertManager =
                new AlertManager();

        SelfHealEventPublisher publisher =
                new SelfHealEventPublisher(
                        alertManager
                );

        AtomicInteger counter =
                new AtomicInteger();

        publisher.registerListener(
                event -> counter.incrementAndGet()
        );

        publisher.publish(null);

        assertEquals(
                0,
                counter.get()
        );
    }
}
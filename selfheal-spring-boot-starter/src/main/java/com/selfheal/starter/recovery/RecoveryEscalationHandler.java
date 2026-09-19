package com.selfheal.starter.recovery;

import com.selfheal.starter.event.SelfHealEvent;
import com.selfheal.starter.event.SelfHealEventPublisher;
import com.selfheal.starter.event.SelfHealEventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecoveryEscalationHandler {

    private final SelfHealEventPublisher eventPublisher;

    private final List<RecoveryEscalation> escalations =
            Collections.synchronizedList(
                    new ArrayList<>()
            );


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RecoveryEscalationHandler(
            SelfHealEventPublisher eventPublisher) {

        if (eventPublisher == null) {
            throw new IllegalArgumentException(
                    "Event publisher cannot be null"
            );
        }

        this.eventPublisher = eventPublisher;
    }


    // =========================================================
    // ESCALATE
    // =========================================================

    public RecoveryEscalation escalate(
            String componentName,
            RecoveryContext context,
            RecoveryResult result,
            String recoveryStrategy) {

        if (componentName == null
                || componentName.isBlank()) {

            throw new IllegalArgumentException(
                    "Component name cannot be empty"
            );
        }

        if (context == null) {
            throw new IllegalArgumentException(
                    "Recovery context cannot be null"
            );
        }

        if (result == null) {
            throw new IllegalArgumentException(
                    "Recovery result cannot be null"
            );
        }

        String reason =
                "Recovery exhausted after "
                        + result.getAttempts()
                        + " attempts";

        RecoveryEscalation escalation =
                new RecoveryEscalation(
                        componentName,
                        context.getFailureType(),
                        recoveryStrategy,
                        result.getAttempts(),
                        result.getDuration(),
                        reason
                );

        escalations.add(escalation);


        // -----------------------------------------------------
        // LOG
        // -----------------------------------------------------

        System.out.println(
                "[SELFHEAL-ESCALATION] Recovery exhausted"
                        + " | component="
                        + componentName
                        + " | failureType="
                        + context.getFailureType()
                        + " | attempts="
                        + result.getAttempts()
        );


        // -----------------------------------------------------
        // EVENT
        // -----------------------------------------------------

        eventPublisher.publish(
                new SelfHealEvent(
                        SelfHealEventType.RECOVERY_FAILED,
                        componentName,
                        "Recovery escalated"
                                + " | failureType="
                                + context.getFailureType()
                                + " | attempts="
                                + result.getAttempts()
                                + " | reason="
                                + reason
                )
        );

        return escalation;
    }


    // =========================================================
    // GET ESCALATIONS
    // =========================================================

    public List<RecoveryEscalation> getEscalations() {

        synchronized (escalations) {

            return new ArrayList<>(escalations);
        }
    }


    // =========================================================
    // GET COUNT
    // =========================================================

    public int getEscalationCount() {

        synchronized (escalations) {

            return escalations.size();
        }
    }


    // =========================================================
    // CLEAR
    // =========================================================

    public void clear() {

        synchronized (escalations) {

            escalations.clear();
        }
    }
}
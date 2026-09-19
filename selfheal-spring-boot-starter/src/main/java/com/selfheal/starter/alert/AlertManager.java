package com.selfheal.starter.alert;

import com.selfheal.starter.event.SelfHealEvent;
import com.selfheal.starter.event.SelfHealEventType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AlertManager {

    private final List<AlertNotifier> notifiers =
            new CopyOnWriteArrayList<>();

    public void registerNotifier(AlertNotifier notifier) {

        if (notifier == null) {
            throw new IllegalArgumentException(
                    "Alert notifier cannot be null"
            );
        }

        notifiers.add(notifier);
    }

    public void handleEvent(SelfHealEvent event) {

        if (event == null) {
            return;
        }

        AlertSeverity severity =
                determineSeverity(event.getType());

        if (severity == null) {
            return;
        }

        SelfHealAlert alert =
                new SelfHealAlert(
                        event.getComponentName(),
                        event.getType(),
                        severity,
                        event.getMessage()
                );

        notifyAll(alert);
    }

    private AlertSeverity determineSeverity(
            SelfHealEventType eventType) {

        return switch (eventType) {

            case FAILURE_DETECTED ->
                    AlertSeverity.WARNING;

            case RECOVERY_STARTED ->
                    AlertSeverity.INFO;

            case RECOVERY_SUCCESS ->
                    AlertSeverity.INFO;

            case RECOVERY_FAILED ->
                    AlertSeverity.CRITICAL;

            default ->
                    null;
        };
    }

    private void notifyAll(SelfHealAlert alert) {

        for (AlertNotifier notifier : notifiers) {

            try {

                notifier.notify(alert);

            } catch (Exception exception) {

                System.err.println(
                        "[SELFHEAL-ALERT] "
                                + "Notifier failed: "
                                + notifier.getName()
                                + " - "
                                + exception.getMessage()
                );
            }
        }
    }

    public int getNotifierCount() {
        return notifiers.size();
    }
}
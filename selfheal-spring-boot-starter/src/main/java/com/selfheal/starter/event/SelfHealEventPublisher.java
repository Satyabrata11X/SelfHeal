package com.selfheal.starter.event;

import com.selfheal.starter.alert.AlertManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SelfHealEventPublisher {

    private final AlertManager alertManager;

    private final List<SelfHealEventListener> listeners =
            new CopyOnWriteArrayList<>();

    public SelfHealEventPublisher(
            AlertManager alertManager) {

        if (alertManager == null) {
            throw new IllegalArgumentException(
                    "Alert manager cannot be null"
            );
        }

        this.alertManager = alertManager;
    }

    public void publish(SelfHealEvent event) {

        if (event == null) {
            return;
        }

        // Existing SelfHeal event logging
        System.out.println(
                "[SELFHEAL-EVENT] "
                        + event.getTimestamp()
                        + " | "
                        + event.getType()
                        + " | "
                        + event.getComponentName()
                        + " | "
                        + event.getMessage()
        );

        // Feature #5 - Alert & Notification
        alertManager.handleEvent(event);

        // Feature #6 - Application Event Listeners
        notifyListeners(event);
    }

    public void registerListener(
            SelfHealEventListener listener) {

        if (listener == null) {
            throw new IllegalArgumentException(
                    "Event listener cannot be null"
            );
        }

        listeners.add(listener);
    }

    public void unregisterListener(
            SelfHealEventListener listener) {

        if (listener == null) {
            return;
        }

        listeners.remove(listener);
    }

    public int getListenerCount() {
        return listeners.size();
    }

    private void notifyListeners(SelfHealEvent event) {

        for (SelfHealEventListener listener : listeners) {

            try {

                listener.onEvent(event);

            } catch (Exception exception) {

                System.err.println(
                        "[SELFHEAL-EVENT] "
                                + "Listener failed: "
                                + listener.getClass().getName()
                                + " - "
                                + exception.getMessage()
                );
            }
        }
    }
}
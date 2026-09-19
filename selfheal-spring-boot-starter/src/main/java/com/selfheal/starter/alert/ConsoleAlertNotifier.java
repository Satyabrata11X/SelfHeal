package com.selfheal.starter.alert;

public class ConsoleAlertNotifier
        implements AlertNotifier {

    @Override
    public String getName() {
        return "console";
    }

    @Override
    public void notify(SelfHealAlert alert) {

        System.out.println(
                "[SELFHEAL-ALERT] "
                        + "severity=" + alert.getSeverity()
                        + ", component=" + alert.getComponentName()
                        + ", event=" + alert.getEventType()
                        + ", message=" + alert.getMessage()
        );
    }
}
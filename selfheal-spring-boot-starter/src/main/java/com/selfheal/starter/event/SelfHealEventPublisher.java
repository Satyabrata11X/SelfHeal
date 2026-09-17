package com.selfheal.starter.event;

public class SelfHealEventPublisher {

    public void publish(SelfHealEvent event) {

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
    }
}
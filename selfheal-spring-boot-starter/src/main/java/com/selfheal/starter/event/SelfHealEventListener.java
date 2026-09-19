package com.selfheal.starter.event;

@FunctionalInterface
public interface SelfHealEventListener {

    void onEvent(SelfHealEvent event);
}
package com.selfheal.starter.alert;

public interface AlertNotifier {

    String getName();

    void notify(SelfHealAlert alert);
}
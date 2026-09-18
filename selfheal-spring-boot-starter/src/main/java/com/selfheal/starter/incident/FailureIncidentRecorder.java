package com.selfheal.starter.incident;

public interface FailureIncidentRecorder {

    void record(FailureContext context);
}
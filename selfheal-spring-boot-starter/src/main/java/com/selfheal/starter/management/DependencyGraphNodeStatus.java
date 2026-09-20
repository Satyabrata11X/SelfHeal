package com.selfheal.starter.management;

import com.selfheal.starter.dependency.DependencyStatus;
import com.selfheal.starter.dependency.DependencyType;

public class DependencyGraphNodeStatus {

    private final String name;
    private final DependencyType type;
    private final DependencyStatus status;
    private final long responseTime;
    private final String message;

    public DependencyGraphNodeStatus(
            String name,
            DependencyType type,
            DependencyStatus status,
            long responseTime,
            String message) {

        this.name = name;
        this.type = type;
        this.status = status;
        this.responseTime = responseTime;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public DependencyType getType() {
        return type;
    }

    public DependencyStatus getStatus() {
        return status;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public String getMessage() {
        return message;
    }
}
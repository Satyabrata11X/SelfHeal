package com.selfheal.starter.dependency;

public class Dependency {

    private final String name;

    private final DependencyType type;

    private volatile DependencyStatus status;

    private volatile long responseTime;

    private volatile String message;


    public Dependency(
            String name,
            DependencyType type) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Dependency name cannot be empty"
            );
        }

        if (type == null) {
            throw new IllegalArgumentException(
                    "Dependency type cannot be null"
            );
        }

        this.name = name;
        this.type = type;
        this.status = DependencyStatus.UNKNOWN;
        this.responseTime = 0L;
        this.message = null;
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


    public void setStatus(
            DependencyStatus status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Dependency status cannot be null"
            );
        }

        this.status = status;
    }


    public long getResponseTime() {
        return responseTime;
    }


    public void setResponseTime(
            long responseTime) {

        if (responseTime < 0) {
            throw new IllegalArgumentException(
                    "Response time cannot be negative"
            );
        }

        this.responseTime = responseTime;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public boolean isAvailable() {

        return status == DependencyStatus.UP;
    }
}
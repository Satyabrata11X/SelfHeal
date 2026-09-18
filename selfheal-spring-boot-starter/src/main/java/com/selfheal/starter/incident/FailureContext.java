package com.selfheal.starter.incident;

import com.selfheal.starter.failure.FailureType;

import java.time.Instant;

public class FailureContext {

    private final String applicationName;

    private final String componentName;

    private final String packageName;

    private final String className;

    private final String methodName;

    private final String fileName;

    private final int lineNumber;

    private final String exceptionType;

    private final FailureType failureType;

    private final String message;

    private final String stackTrace;

    private final Instant timestamp;

    public FailureContext(
            String applicationName,
            String componentName,
            String packageName,
            String className,
            String methodName,
            String fileName,
            int lineNumber,
            String exceptionType,
            FailureType failureType,
            String message,
            String stackTrace) {

        this.applicationName = applicationName;
        this.componentName = componentName;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.exceptionType = exceptionType;
        this.failureType = failureType;
        this.message = message;
        this.stackTrace = stackTrace;
        this.timestamp = Instant.now();
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public FailureType getFailureType() {
        return failureType;
    }

    public String getMessage() {
        return message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
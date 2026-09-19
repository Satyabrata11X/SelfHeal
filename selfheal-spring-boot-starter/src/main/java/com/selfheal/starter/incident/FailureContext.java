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

    private final String fingerprint;


    // =========================================================
    // EXISTING CONSTRUCTOR
    // =========================================================

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

        this(
                applicationName,
                componentName,
                packageName,
                className,
                methodName,
                fileName,
                lineNumber,
                exceptionType,
                failureType,
                message,
                stackTrace,
                null
        );
    }


    // =========================================================
    // CONSTRUCTOR WITH FINGERPRINT
    // =========================================================

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
            String stackTrace,
            String fingerprint) {

        this(
                applicationName,
                componentName,
                packageName,
                className,
                methodName,
                fileName,
                lineNumber,
                exceptionType,
                failureType,
                message,
                stackTrace,
                fingerprint,
                Instant.now()
        );
    }


    // =========================================================
    // INTERNAL CONSTRUCTOR
    // =========================================================

    private FailureContext(
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
            String stackTrace,
            String fingerprint,
            Instant timestamp) {

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
        this.fingerprint = fingerprint;
        this.timestamp = timestamp;
    }


    // =========================================================
    // ADD FINGERPRINT
    // =========================================================

    public FailureContext withFingerprint(
            String fingerprint) {

        return new FailureContext(
                applicationName,
                componentName,
                packageName,
                className,
                methodName,
                fileName,
                lineNumber,
                exceptionType,
                failureType,
                message,
                stackTrace,
                fingerprint,
                timestamp
        );
    }


    // =========================================================
    // GETTERS
    // =========================================================

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

    public String getFingerprint() {
        return fingerprint;
    }
}
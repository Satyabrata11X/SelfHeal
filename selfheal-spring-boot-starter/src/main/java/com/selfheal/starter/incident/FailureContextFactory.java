package com.selfheal.starter.incident;

import com.selfheal.starter.failure.FailureInfo;

import java.io.PrintWriter;
import java.io.StringWriter;

public class FailureContextFactory {

    private final StackTraceLocationExtractor locationExtractor;

    public FailureContextFactory(
            StackTraceLocationExtractor locationExtractor) {

        this.locationExtractor = locationExtractor;
    }

    public FailureContext create(
            String applicationName,
            FailureInfo failureInfo,
            Throwable throwable) {

        if (failureInfo == null) {
            throw new IllegalArgumentException(
                    "FailureInfo cannot be null"
            );
        }

        StackTraceLocationExtractor.StackTraceLocation location =
                locationExtractor.extract(throwable);

        String stackTrace =
                buildStackTrace(throwable);

        String exceptionType =
                throwable != null
                        ? throwable.getClass().getName()
                        : null;

        return new FailureContext(
                applicationName,
                failureInfo.getComponentName(),
                location.getPackageName(),
                location.getClassName(),
                location.getMethodName(),
                location.getFileName(),
                location.getLineNumber(),
                exceptionType,
                failureInfo.getType(),
                failureInfo.getMessage(),
                stackTrace
        );
    }

    private String buildStackTrace(
            Throwable throwable) {

        if (throwable == null) {
            return null;
        }

        StringWriter stringWriter =
                new StringWriter();

        PrintWriter printWriter =
                new PrintWriter(stringWriter);

        throwable.printStackTrace(printWriter);

        printWriter.flush();

        return stringWriter.toString();
    }
}
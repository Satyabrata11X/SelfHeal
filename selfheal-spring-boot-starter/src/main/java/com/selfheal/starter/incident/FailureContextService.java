package com.selfheal.starter.incident;

import com.selfheal.starter.failure.FailureInfo;

public class FailureContextService {

    private final FailureContextFactory contextFactory;

    public FailureContextService(
            FailureContextFactory contextFactory) {

        this.contextFactory = contextFactory;
    }

    public FailureContext capture(
            String applicationName,
            FailureInfo failureInfo,
            Throwable throwable) {

        return contextFactory.create(
                applicationName,
                failureInfo,
                throwable
        );
    }
}
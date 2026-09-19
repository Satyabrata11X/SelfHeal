package com.selfheal.starter.incident;

import com.selfheal.starter.failure.FailureInfo;
import com.selfheal.starter.fingerprint.FailureFingerprint;
import com.selfheal.starter.fingerprint.FailureFingerprintGenerator;

public class FailureContextService {

    private final FailureContextFactory contextFactory;

    private final FailureFingerprintGenerator fingerprintGenerator;


    public FailureContextService(
            FailureContextFactory contextFactory,
            FailureFingerprintGenerator fingerprintGenerator) {

        if (contextFactory == null) {
            throw new IllegalArgumentException(
                    "Failure context factory cannot be null"
            );
        }

        if (fingerprintGenerator == null) {
            throw new IllegalArgumentException(
                    "Failure fingerprint generator cannot be null"
            );
        }

        this.contextFactory = contextFactory;
        this.fingerprintGenerator = fingerprintGenerator;
    }


    public FailureContext capture(
            String applicationName,
            FailureInfo failureInfo,
            Throwable throwable) {

        FailureContext context =
                contextFactory.create(
                        applicationName,
                        failureInfo,
                        throwable
                );

        FailureFingerprint fingerprint =
                fingerprintGenerator.generate(context);

        return context.withFingerprint(
                fingerprint.getFingerprint()
        );
    }
}
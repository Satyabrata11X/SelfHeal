package com.selfheal.starter.fingerprint;

import com.selfheal.starter.incident.FailureContext;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FailureFingerprintGenerator {

    public FailureFingerprint generate(
            FailureContext context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Failure context cannot be null"
            );
        }

        String componentName =
                normalize(context.getComponentName());

        String exceptionType =
                normalize(context.getExceptionType());

        String failureType =
                context.getFailureType() == null
                        ? "UNKNOWN"
                        : context.getFailureType().name();

        String location =
                buildLocation(context);

        String fingerprintInput =
                componentName
                        + "|"
                        + exceptionType
                        + "|"
                        + failureType
                        + "|"
                        + location;

        String fingerprint =
                sha256(fingerprintInput);

        return new FailureFingerprint(
                fingerprint,
                componentName,
                exceptionType,
                failureType,
                location
        );
    }

    private String buildLocation(
            FailureContext context) {

        String packageName =
                normalize(context.getPackageName());

        String className =
                normalize(context.getClassName());

        String methodName =
                normalize(context.getMethodName());

        return packageName
                + "."
                + className
                + "#"
                + methodName;
    }

    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return "UNKNOWN";
        }

        return value.trim();
    }

    private String sha256(String value) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder result =
                    new StringBuilder();

            for (byte b : hash) {

                result.append(
                        String.format(
                                "%02x",
                                b
                        )
                );
            }

            return result.toString();

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    exception
            );
        }
    }
}
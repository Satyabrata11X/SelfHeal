package com.selfheal.starter.failure;

import com.selfheal.starter.core.HealthCheck;
import com.selfheal.starter.core.HealthCheckResult;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class FailureClassifier {

    public FailureInfo classify(
            HealthCheck healthCheck,
            HealthCheckResult result) {

        if (result == null) {

            return new FailureInfo(
                    FailureType.UNKNOWN,
                    healthCheck.getName(),
                    "Health check returned null result"
            );
        }

        if (result.getStatus()
                != com.selfheal.starter.core.HealthStatus.DOWN) {

            return null;
        }

        FailureType failureType =
                result.getFailureType();

        if (failureType == null) {
            failureType =
                    FailureType.COMPONENT_FAILURE;
        }

        return new FailureInfo(
                failureType,
                healthCheck.getName(),
                result.getMessage()
        );
    }

    public FailureInfo classify(
            HealthCheck healthCheck,
            Exception exception) {

        FailureType failureType =
                determineFailureType(exception);

        String message =
                exception.getMessage();

        if (message == null || message.isBlank()) {

            message =
                    exception
                            .getClass()
                            .getSimpleName();
        }

        return new FailureInfo(
                failureType,
                healthCheck.getName(),
                message
        );
    }

    private FailureType determineFailureType(
            Exception exception) {

        if (exception instanceof SocketTimeoutException
                || exception instanceof TimeoutException) {

            return FailureType.TIMEOUT;
        }

        if (exception instanceof ConnectException) {

            return FailureType.CONNECTION_ERROR;
        }

        if (exception instanceof SQLException) {

            return FailureType.DATABASE_FAILURE;
        }

        return FailureType.UNKNOWN;
    }
}
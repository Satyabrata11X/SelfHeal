package com.selfheal.starter.incident;

import com.selfheal.starter.failure.FailureInfo;
import com.selfheal.starter.failure.FailureType;
import com.selfheal.starter.persistence.FailureIncidentPersistence;

import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SelfHealExceptionInterceptor
        implements HandlerExceptionResolver {

    private final FailureContextService contextService;

    private final FailureIncidentPersistence persistence;

    private final Environment environment;

    public SelfHealExceptionInterceptor(
            FailureContextService contextService,
            FailureIncidentPersistence persistence,
            Environment environment) {

        this.contextService = contextService;
        this.persistence = persistence;
        this.environment = environment;
    }

    @Override
    public ModelAndView resolveException(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception) {

        System.out.println(
                "[SELFHEAL-DEBUG] Exception intercepted: "
                        + exception.getClass().getName()
        );

        try {

            String applicationName =
                    environment.getProperty(
                            "spring.application.name",
                            "selfheal-application"
                    );

            String componentName =
                    handler != null
                            ? handler.getClass().getName()
                            : "unknown";

            FailureInfo failureInfo =
                    new FailureInfo(
                            FailureType.UNKNOWN,
                            componentName,
                            exception.getMessage()
                    );

            FailureContext context =
                    contextService.capture(
                            applicationName,
                            failureInfo,
                            exception
                    );

            persistence.save(context);

        } catch (Exception captureException) {

            System.err.println(
                    "[SELFHEAL-INCIDENT] "
                            + "Failed to persist incident: "
                            + captureException.getMessage()
            );
        }

        // IMPORTANT:
        // Return null so Spring's normal exception
        // handling continues.
        return null;
    }
}
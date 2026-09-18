package com.selfheal.starter.incident;

import com.selfheal.starter.failure.FailureInfo;
import com.selfheal.starter.failure.FailureType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class SelfHealExceptionInterceptor
        implements HandlerExceptionResolver {

    private final FailureContextService contextService;
    private final FailureIncidentRecorder incidentRecorder;
    private final Environment environment;

    public SelfHealExceptionInterceptor(
            FailureContextService contextService,
            FailureIncidentRecorder incidentRecorder,
            Environment environment) {

        this.contextService = contextService;
        this.incidentRecorder = incidentRecorder;
        this.environment = environment;
    }

    @Override
    public ModelAndView resolveException(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception) {

        try {

            // Get the application name from the actual running
            // Spring Boot application's Environment.
            String applicationName =
                    environment.getProperty(
                            "spring.application.name"
                    );

            if (applicationName == null
                    || applicationName.isBlank()) {

                applicationName = "selfheal-application";
            }

            String componentName =
                    resolveComponentName(handler);

            String message = exception.getMessage();

            if (message == null || message.isBlank()) {
                message = exception.getClass().getSimpleName();
            }

            FailureInfo failureInfo =
                    new FailureInfo(
                            FailureType.UNKNOWN,
                            componentName,
                            message
                    );

            FailureContext context =
                    contextService.capture(
                            applicationName,
                            failureInfo,
                            exception
                    );

            incidentRecorder.record(context);

            System.out.println(
                    "[SELFHEAL] Incident captured"
            );

            System.out.println(
                    "[SELFHEAL] Application: "
                            + applicationName
            );

        } catch (Exception captureException) {

            System.err.println(
                    "[SELFHEAL] Failed to capture exception context"
            );

            captureException.printStackTrace();
        }

        // Return null so Spring's normal exception handling
        // continues.
        return null;
    }

    private String resolveComponentName(Object handler) {

        if (handler == null) {
            return "unknown-handler";
        }

        return handler.getClass().getName();
    }
}
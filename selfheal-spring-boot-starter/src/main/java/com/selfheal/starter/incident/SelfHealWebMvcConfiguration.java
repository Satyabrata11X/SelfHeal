package com.selfheal.starter.incident;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

public class SelfHealWebMvcConfiguration
        implements WebMvcConfigurer {

    private final HandlerExceptionResolver
            selfHealExceptionInterceptor;

    public SelfHealWebMvcConfiguration(
            HandlerExceptionResolver selfHealExceptionInterceptor) {

        this.selfHealExceptionInterceptor =
                selfHealExceptionInterceptor;
    }

    @Override
    public void extendHandlerExceptionResolvers(
            List<HandlerExceptionResolver> resolvers) {

        resolvers.add(
                0,
                selfHealExceptionInterceptor
        );
    }
}
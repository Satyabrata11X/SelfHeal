package com.selfheal.selfheal_demo_app.config;

import com.selfheal.starter.dependency.Dependency;
import com.selfheal.starter.dependency.DependencyGraphRegistrationService;
import com.selfheal.starter.dependency.DependencyStatus;
import com.selfheal.starter.dependency.DependencyType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDependencyConfiguration {

    @Bean
    public DemoDependencyInitializer demoDependencyInitializer(
            DependencyGraphRegistrationService registrationService) {

        return new DemoDependencyInitializer(
                registrationService
        );
    }

    public static class DemoDependencyInitializer {

        private final DependencyGraphRegistrationService registrationService;

        public DemoDependencyInitializer(
                DependencyGraphRegistrationService registrationService) {

            this.registrationService =
                    registrationService;
        }

        @jakarta.annotation.PostConstruct
        public void initialize() {

            Dependency postgres =
                    new Dependency(
                            "postgresql",
                            DependencyType.DATABASE
                    );

            postgres.setStatus(
                    DependencyStatus.UP
            );

            postgres.setMessage(
                    "PostgreSQL is available"
            );

            postgres.setResponseTime(12);

            registrationService.register(
                    "selfheal-demo-component",
                    postgres
            );


            Dependency redis =
                    new Dependency(
                            "redis",
                            DependencyType.CACHE
                    );

            redis.setStatus(
                    DependencyStatus.UP
            );

            redis.setMessage(
                    "Redis is available"
            );

            redis.setResponseTime(5);

            registrationService.register(
                    "selfheal-demo-component",
                    redis
            );


            Dependency paymentApi =
                    new Dependency(
                            "payment-api",
                            DependencyType.REST_API
                    );

            paymentApi.setStatus(
                    DependencyStatus.UP
            );

            paymentApi.setMessage(
                    "Payment API is available"
            );

            paymentApi.setResponseTime(35);

            registrationService.register(
                    "selfheal-demo-component",
                    paymentApi
            );
        }
    }
}
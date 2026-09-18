package com.selfheal.selfheal_demo_app.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class ApplicationInfoController {

    private final Environment environment;

    public ApplicationInfoController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/application")
    public Map<String, Object> applicationInfo() {

        Map<String, Object> result = new LinkedHashMap<>();

        result.put(
                "spring.application.name",
                environment.getProperty("spring.application.name")
        );

        result.put(
                "spring.application.version",
                environment.getProperty("spring.application.version")
        );

        result.put(
                "spring.profiles.active",
                environment.getProperty("spring.profiles.active")
        );

        return result;
    }
}
package com.selfheal.selfheal_demo_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/selfheal/test/exception")
public class FailureSimulationController {

    @GetMapping("/database")
    public String simulateDatabaseFailure() {

        return simulateDatabaseOperation();
    }

    private String simulateDatabaseOperation() {

        throw new RuntimeException(
                "Simulated database connection failure"
        );
    }
}
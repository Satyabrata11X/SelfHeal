package com.selfheal.selfheal_demo_app.controller;

import com.selfheal.starter.core.HealthCheckResult;
import com.selfheal.starter.core.SelfHealComponent;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/selfheal/test")
public class SelfHealTestController {

    private final SelfHealComponent component;

    public SelfHealTestController(
            SelfHealComponent component) {

        this.component = component;
    }

    @GetMapping("/status")
    public HealthCheckResult status() {
        return component.check();
    }

    @PostMapping("/fail")
    public String fail() {

        component.fail();

        return "SELFHEAL COMPONENT: FAILURE SIMULATED";
    }

    @PostMapping("/recover")
    public String recover() {

        component.recover();

        return "SELFHEAL COMPONENT: MANUALLY RECOVERED";
    }

    @PostMapping("/latency/{milliseconds}")
    public String setLatency(
            @PathVariable long milliseconds) {

        component.setSimulatedLatency(milliseconds);

        return "SELFHEAL COMPONENT: SIMULATED LATENCY SET TO "
                + milliseconds
                + "ms";
    }

    @DeleteMapping("/latency")
    public String clearLatency() {

        component.clearSimulatedLatency();

        return "SELFHEAL COMPONENT: SIMULATED LATENCY CLEARED";
    }

    @GetMapping("/latency")
    public String getLatency() {

        return "Current simulated latency: "
                + component.getSimulatedLatency()
                + "ms";
    }
}
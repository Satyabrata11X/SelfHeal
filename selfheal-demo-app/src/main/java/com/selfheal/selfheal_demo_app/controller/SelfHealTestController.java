package com.selfheal.selfheal_demo_app.controller;

import com.selfheal.starter.core.SelfHealComponent;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/selfheal/test")
public class SelfHealTestController {

    private final SelfHealComponent component;

    public SelfHealTestController(SelfHealComponent component) {
        this.component = component;
    }

    @GetMapping("/status")
    public String status() {
        return component.isHealthy()
                ? "SELFHEAL COMPONENT: HEALTHY"
                : "SELFHEAL COMPONENT: FAILED";
    }

    @PostMapping("/fail")
    public String fail() {
        component.fail();

        return "SELFHEAL COMPONENT: FAILURE SIMULATED";
    }
}
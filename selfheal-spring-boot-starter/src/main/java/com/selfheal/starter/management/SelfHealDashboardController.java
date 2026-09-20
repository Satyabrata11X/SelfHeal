package com.selfheal.starter.management;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SelfHealDashboardController {

    @GetMapping("/selfheal/dashboard")
    public String dashboard() {

        return "selfheal/dashboard";
    }
}
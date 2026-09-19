package com.selfheal.starter.incident;

import com.selfheal.starter.persistence.FailureIncidentPersistence;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/selfheal/incidents")
public class SelfHealIncidentController {

    private final FailureIncidentPersistence persistence;

    public SelfHealIncidentController(
            FailureIncidentPersistence persistence) {

        this.persistence = persistence;
    }

    @GetMapping
    public java.util.List<FailureContext> getIncidents() {
        return persistence.findAll();
    }

    @DeleteMapping
    public void clearIncidents() {
        persistence.deleteAll();
    }
}
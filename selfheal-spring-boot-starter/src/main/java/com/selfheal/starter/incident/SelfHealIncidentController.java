package com.selfheal.starter.incident;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/selfheal/incidents")
public class SelfHealIncidentController {

    private final InMemoryFailureIncidentRecorder recorder;

    public SelfHealIncidentController(
            InMemoryFailureIncidentRecorder recorder) {

        this.recorder = recorder;
    }

    @GetMapping
    public List<FailureContext> getIncidents() {

        return recorder.getIncidents();
    }

    @DeleteMapping
    public String clearIncidents() {

        recorder.clear();

        return "SELFHEAL INCIDENTS CLEARED";
    }
}
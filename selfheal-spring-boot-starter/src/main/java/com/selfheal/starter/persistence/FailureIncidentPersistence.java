package com.selfheal.starter.persistence;

import com.selfheal.starter.incident.FailureContext;

import java.util.List;

public interface FailureIncidentPersistence {

    void save(FailureContext context);

    List<FailureContext> findAll();

    void deleteAll();
}
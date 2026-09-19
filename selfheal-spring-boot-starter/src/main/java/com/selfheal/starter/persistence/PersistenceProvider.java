package com.selfheal.starter.persistence;

public interface PersistenceProvider {

    String getName();

    FailureIncidentPersistence getPersistence();
}
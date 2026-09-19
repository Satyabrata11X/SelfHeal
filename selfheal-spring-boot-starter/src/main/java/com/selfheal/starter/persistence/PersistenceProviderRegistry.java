package com.selfheal.starter.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceProviderRegistry {

    private final Map<String, FailureIncidentPersistence> providers =
            new ConcurrentHashMap<>();

    public void register(
            String name,
            FailureIncidentPersistence persistence) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Persistence provider name cannot be empty"
            );
        }

        if (persistence == null) {
            throw new IllegalArgumentException(
                    "Persistence implementation cannot be null"
            );
        }

        providers.put(
                name.toLowerCase(),
                persistence
        );

        System.out.println(
                "[SELFHEAL-PERSISTENCE] Provider registered: "
                        + name.toLowerCase()
        );
    }

    public FailureIncidentPersistence get(
            String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Persistence provider name cannot be empty"
            );
        }

        FailureIncidentPersistence persistence =
                providers.get(name.toLowerCase());

        if (persistence == null) {
            throw new IllegalStateException(
                    "No persistence provider registered with name: "
                            + name
            );
        }

        return persistence;
    }

    public boolean contains(String name) {

        if (name == null || name.isBlank()) {
            return false;
        }

        return providers.containsKey(
                name.toLowerCase()
        );
    }
}
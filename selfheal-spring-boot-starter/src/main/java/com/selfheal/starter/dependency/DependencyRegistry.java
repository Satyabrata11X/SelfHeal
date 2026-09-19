package com.selfheal.starter.dependency;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyRegistry {

    private final Map<String, Dependency> dependencies =
            new ConcurrentHashMap<>();


    public void register(Dependency dependency) {

        if (dependency == null) {
            throw new IllegalArgumentException(
                    "Dependency cannot be null"
            );
        }

        dependencies.put(
                dependency.getName(),
                dependency
        );
    }


    public Dependency get(String name) {

        if (name == null || name.isBlank()) {
            return null;
        }

        return dependencies.get(name);
    }


    public boolean contains(String name) {

        return name != null
                && dependencies.containsKey(name);
    }


    public void remove(String name) {

        if (name == null || name.isBlank()) {
            return;
        }

        dependencies.remove(name);
    }


    public void clear() {

        dependencies.clear();
    }


    public int size() {

        return dependencies.size();
    }


    public Map<String, Dependency> getAll() {

        return Collections.unmodifiableMap(
                dependencies
        );
    }
}
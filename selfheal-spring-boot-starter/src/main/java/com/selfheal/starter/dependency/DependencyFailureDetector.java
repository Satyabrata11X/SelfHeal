package com.selfheal.starter.dependency;

import java.util.ArrayList;
import java.util.List;

public class DependencyFailureDetector {

    private final DependencyRegistry registry;


    public DependencyFailureDetector(
            DependencyRegistry registry) {

        if (registry == null) {
            throw new IllegalArgumentException(
                    "Dependency registry cannot be null"
            );
        }

        this.registry = registry;
    }


    public boolean isHealthy(
            String dependencyName) {

        Dependency dependency =
                registry.get(dependencyName);

        if (dependency == null) {
            return false;
        }

        return dependency.isAvailable();
    }


    public List<Dependency> getFailedDependencies() {

        List<Dependency> failed =
                new ArrayList<>();

        for (Dependency dependency :
                registry.getAll().values()) {

            if (!dependency.isAvailable()) {
                failed.add(dependency);
            }
        }

        return failed;
    }


    public List<Dependency> getHealthyDependencies() {

        List<Dependency> healthy =
                new ArrayList<>();

        for (Dependency dependency :
                registry.getAll().values()) {

            if (dependency.isAvailable()) {
                healthy.add(dependency);
            }
        }

        return healthy;
    }


    public boolean hasFailures() {

        return !getFailedDependencies().isEmpty();
    }


    public int getFailureCount() {

        return getFailedDependencies().size();
    }
}
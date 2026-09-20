package com.selfheal.starter.dependency;

public class DependencyGraphRegistrationService {

    private final DependencyRegistry dependencyRegistry;

    private final DependencyGraphService dependencyGraphService;

    public DependencyGraphRegistrationService(
            DependencyRegistry dependencyRegistry,
            DependencyGraphService dependencyGraphService) {

        if (dependencyRegistry == null) {
            throw new IllegalArgumentException(
                    "Dependency registry cannot be null"
            );
        }

        if (dependencyGraphService == null) {
            throw new IllegalArgumentException(
                    "Dependency graph service cannot be null"
            );
        }

        this.dependencyRegistry = dependencyRegistry;
        this.dependencyGraphService = dependencyGraphService;
    }

    public void register(
            String componentName,
            Dependency dependency) {

        if (componentName == null || componentName.isBlank()) {
            throw new IllegalArgumentException(
                    "Component name cannot be empty"
            );
        }

        if (dependency == null) {
            throw new IllegalArgumentException(
                    "Dependency cannot be null"
            );
        }

        /*
         * Existing dependency system remains the source
         * of dependency state.
         */
        dependencyRegistry.register(dependency);

        /*
         * Graph stores only the topology.
         */
        dependencyGraphService.addDependencyRelationship(
                componentName,
                dependency
        );
    }
}
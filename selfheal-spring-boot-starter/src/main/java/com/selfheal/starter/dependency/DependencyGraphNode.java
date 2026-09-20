package com.selfheal.starter.dependency;

public class DependencyGraphNode {

    private final String name;

    private final DependencyType type;

    public DependencyGraphNode(
            String name,
            DependencyType type) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Graph node name cannot be empty"
            );
        }

        if (type == null) {
            throw new IllegalArgumentException(
                    "Graph node type cannot be null"
            );
        }

        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public DependencyType getType() {
        return type;
    }
}
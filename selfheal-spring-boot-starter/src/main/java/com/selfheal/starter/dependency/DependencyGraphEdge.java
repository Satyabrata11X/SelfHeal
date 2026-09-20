package com.selfheal.starter.dependency;

public class DependencyGraphEdge {

    private final String source;

    private final String target;

    public DependencyGraphEdge(
            String source,
            String target) {

        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException(
                    "Graph edge source cannot be empty"
            );
        }

        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException(
                    "Graph edge target cannot be empty"
            );
        }

        if (source.equals(target)) {
            throw new IllegalArgumentException(
                    "Graph edge source and target cannot be the same"
            );
        }

        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }
}
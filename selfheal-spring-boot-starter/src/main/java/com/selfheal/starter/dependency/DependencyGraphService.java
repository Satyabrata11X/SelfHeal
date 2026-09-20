package com.selfheal.starter.dependency;

import com.selfheal.starter.management.DependencyGraphNodeStatus;
import com.selfheal.starter.management.DependencyGraphResponse;

import java.util.ArrayList;
import java.util.List;

public class DependencyGraphService {

    private final DependencyRegistry dependencyRegistry;

    private final DependencyGraph dependencyGraph;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DependencyGraphService(
            DependencyRegistry dependencyRegistry,
            DependencyGraph dependencyGraph) {

        if (dependencyRegistry == null) {
            throw new IllegalArgumentException(
                    "Dependency registry cannot be null"
            );
        }

        if (dependencyGraph == null) {
            throw new IllegalArgumentException(
                    "Dependency graph cannot be null"
            );
        }

        this.dependencyRegistry = dependencyRegistry;
        this.dependencyGraph = dependencyGraph;
    }


    // =========================================================
    // ADD DEPENDENCY RELATIONSHIP
    // =========================================================

    /**
     * Adds a dependency relationship to the graph.
     *
     * Existing dependency health information remains
     * inside DependencyRegistry.
     */
    public void addDependencyRelationship(
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
         * Component node.
         *
         * A component is not itself a dependency,
         * therefore UNKNOWN is used as its graph node type.
         */
        if (!dependencyGraph.containsNode(componentName)) {

            dependencyGraph.addNode(
                    new DependencyGraphNode(
                            componentName,
                            DependencyType.UNKNOWN
                    )
            );
        }

        /*
         * Dependency node.
         */
        if (!dependencyGraph.containsNode(
                dependency.getName())) {

            dependencyGraph.addNode(
                    new DependencyGraphNode(
                            dependency.getName(),
                            dependency.getType()
                    )
            );
        }

        /*
         * Relationship:
         *
         * component -> dependency
         */
        dependencyGraph.addEdge(
                new DependencyGraphEdge(
                        componentName,
                        dependency.getName()
                )
        );
    }


    // =========================================================
    // GET DEPENDENCY
    // =========================================================

    /**
     * Returns a dependency from the existing registry.
     */
    public Dependency getDependency(
            String dependencyName) {

        return dependencyRegistry.get(
                dependencyName
        );
    }


    // =========================================================
    // DEPENDENCY HEALTH
    // =========================================================

    /**
     * Checks the current health state from the existing
     * DependencyRegistry.
     */
    public boolean isDependencyHealthy(
            String dependencyName) {

        Dependency dependency =
                dependencyRegistry.get(
                        dependencyName
                );

        return dependency != null
                && dependency.isAvailable();
    }


    // =========================================================
    // GET DEPENDENCIES OF COMPONENT
    // =========================================================

    /**
     * Returns all dependencies of a component.
     */
    public List<DependencyGraphEdge> getDependenciesOf(
            String componentName) {

        return dependencyGraph.getDependenciesOf(
                componentName
        );
    }


    // =========================================================
    // GET DEPENDENTS OF DEPENDENCY
    // =========================================================

    /**
     * Returns all components that depend on a dependency.
     */
    public List<DependencyGraphEdge> getDependentsOf(
            String dependencyName) {

        return dependencyGraph.getDependentsOf(
                dependencyName
        );
    }


    // =========================================================
    // HEALTH DEPENDENCY GRAPH
    // =========================================================

    /**
     * Builds a health-aware representation of the dependency
     * graph.
     *
     * The graph itself stores only topology.
     *
     * Current dependency health information is retrieved
     * from DependencyRegistry.
     */
    public DependencyGraphResponse getHealthGraph() {

        List<DependencyGraphNodeStatus> nodes =
                new ArrayList<>();

        for (DependencyGraphNode graphNode :
                dependencyGraph.getNodes().values()) {

            Dependency dependency =
                    dependencyRegistry.get(
                            graphNode.getName()
                    );

            /*
             * The root application/component is represented
             * in the graph but is not necessarily registered
             * as a Dependency.
             */
            if (dependency == null) {

                nodes.add(
                        new DependencyGraphNodeStatus(
                                graphNode.getName(),
                                graphNode.getType(),
                                DependencyStatus.UNKNOWN,
                                0L,
                                "No dependency health information"
                        )
                );

                continue;
            }

            /*
             * Dependency health comes directly from the
             * existing DependencyRegistry.
             */
            nodes.add(
                    new DependencyGraphNodeStatus(
                            dependency.getName(),
                            dependency.getType(),
                            dependency.getStatus(),
                            dependency.getResponseTime(),
                            dependency.getMessage()
                    )
            );
        }

        return new DependencyGraphResponse(
                dependencyGraph.getNodeCount(),
                dependencyGraph.getEdgeCount(),
                nodes,
                dependencyGraph.getEdges()
        );
    }


    // =========================================================
    // GET UNDERLYING GRAPH
    // =========================================================

    /**
     * Returns the underlying dependency graph.
     */
    public DependencyGraph getGraph() {

        return dependencyGraph;
    }


    // =========================================================
    // GET DEPENDENCY REGISTRY
    // =========================================================

    /**
     * Returns the existing dependency registry.
     */
    public DependencyRegistry getRegistry() {

        return dependencyRegistry;
    }
}
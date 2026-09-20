package com.selfheal.starter.dependency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyGraph {

    private final Map<String, DependencyGraphNode> nodes =
            new ConcurrentHashMap<>();

    private final List<DependencyGraphEdge> edges =
            Collections.synchronizedList(
                    new ArrayList<>()
            );


    // =========================================================
    // NODE MANAGEMENT
    // =========================================================

    public void addNode(
            DependencyGraphNode node) {

        if (node == null) {
            throw new IllegalArgumentException(
                    "Graph node cannot be null"
            );
        }

        nodes.put(
                node.getName(),
                node
        );
    }


    public DependencyGraphNode getNode(
            String name) {

        if (name == null || name.isBlank()) {
            return null;
        }

        return nodes.get(name);
    }


    public boolean containsNode(
            String name) {

        return name != null
                && nodes.containsKey(name);
    }


    public void removeNode(
            String name) {

        if (name == null || name.isBlank()) {
            return;
        }

        nodes.remove(name);

        synchronized (edges) {
            edges.removeIf(edge ->
                    edge.getSource().equals(name)
                            || edge.getTarget().equals(name)
            );
        }
    }


    // =========================================================
    // EDGE MANAGEMENT
    // =========================================================

    public void addEdge(
            DependencyGraphEdge edge) {

        if (edge == null) {
            throw new IllegalArgumentException(
                    "Graph edge cannot be null"
            );
        }

        if (!containsNode(edge.getSource())) {
            throw new IllegalArgumentException(
                    "Source node does not exist: "
                            + edge.getSource()
            );
        }

        if (!containsNode(edge.getTarget())) {
            throw new IllegalArgumentException(
                    "Target node does not exist: "
                            + edge.getTarget()
            );
        }

        synchronized (edges) {

            boolean exists = edges.stream()
                    .anyMatch(existing ->
                            existing.getSource()
                                    .equals(edge.getSource())
                                    && existing.getTarget()
                                    .equals(edge.getTarget())
                    );

            if (!exists) {
                edges.add(edge);
            }
        }
    }


    public void removeEdge(
            String source,
            String target) {

        if (source == null || source.isBlank()
                || target == null || target.isBlank()) {
            return;
        }

        synchronized (edges) {

            edges.removeIf(edge ->
                    edge.getSource().equals(source)
                            && edge.getTarget().equals(target)
            );
        }
    }


    // =========================================================
    // READ GRAPH
    // =========================================================

    public Map<String, DependencyGraphNode> getNodes() {

        return Collections.unmodifiableMap(
                nodes
        );
    }


    public List<DependencyGraphEdge> getEdges() {

        synchronized (edges) {
            return new ArrayList<>(edges);
        }
    }


    public List<DependencyGraphEdge> getDependenciesOf(
            String source) {

        List<DependencyGraphEdge> result =
                new ArrayList<>();

        synchronized (edges) {

            for (DependencyGraphEdge edge : edges) {

                if (edge.getSource().equals(source)) {
                    result.add(edge);
                }
            }
        }

        return result;
    }


    public List<DependencyGraphEdge> getDependentsOf(
            String target) {

        List<DependencyGraphEdge> result =
                new ArrayList<>();

        synchronized (edges) {

            for (DependencyGraphEdge edge : edges) {

                if (edge.getTarget().equals(target)) {
                    result.add(edge);
                }
            }
        }

        return result;
    }


    // =========================================================
    // GRAPH OPERATIONS
    // =========================================================

    public void clear() {

        nodes.clear();

        synchronized (edges) {
            edges.clear();
        }
    }


    public int getNodeCount() {
        return nodes.size();
    }


    public int getEdgeCount() {

        synchronized (edges) {
            return edges.size();
        }
    }
}
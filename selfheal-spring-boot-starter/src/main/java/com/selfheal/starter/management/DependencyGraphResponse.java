package com.selfheal.starter.management;

import com.selfheal.starter.dependency.DependencyGraphEdge;

import java.util.List;

public class DependencyGraphResponse {

    private final int nodeCount;
    private final int edgeCount;

    private final List<DependencyGraphNodeStatus> nodes;

    private final List<DependencyGraphEdge> edges;

    public DependencyGraphResponse(
            int nodeCount,
            int edgeCount,
            List<DependencyGraphNodeStatus> nodes,
            List<DependencyGraphEdge> edges) {

        this.nodeCount = nodeCount;
        this.edgeCount = edgeCount;
        this.nodes = nodes;
        this.edges = edges;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public List<DependencyGraphNodeStatus> getNodes() {
        return nodes;
    }

    public List<DependencyGraphEdge> getEdges() {
        return edges;
    }
}
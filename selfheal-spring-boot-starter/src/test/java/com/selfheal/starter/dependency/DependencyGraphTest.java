package com.selfheal.starter.dependency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DependencyGraphTest {

    private DependencyGraph graph;

    @BeforeEach
    void setUp() {
        graph = new DependencyGraph();
    }

    @Test
    void shouldAddAndRetrieveNode() {

        DependencyGraphNode node =
                new DependencyGraphNode(
                        "postgresql",
                        DependencyType.DATABASE
                );

        graph.addNode(node);

        assertTrue(graph.containsNode("postgresql"));
        assertEquals(node, graph.getNode("postgresql"));
        assertEquals(1, graph.getNodeCount());
    }

    @Test
    void shouldAddEdgeBetweenExistingNodes() {

        graph.addNode(
                new DependencyGraphNode(
                        "selfheal-demo-component",
                        DependencyType.UNKNOWN
                )
        );

        graph.addNode(
                new DependencyGraphNode(
                        "postgresql",
                        DependencyType.DATABASE
                )
        );

        DependencyGraphEdge edge =
                new DependencyGraphEdge(
                        "selfheal-demo-component",
                        "postgresql"
                );

        graph.addEdge(edge);

        assertEquals(1, graph.getEdgeCount());

        assertEquals(
                "postgresql",
                graph.getDependenciesOf(
                        "selfheal-demo-component"
                ).get(0).getTarget()
        );
    }

    @Test
    void shouldRejectEdgeWhenSourceNodeDoesNotExist() {

        graph.addNode(
                new DependencyGraphNode(
                        "postgresql",
                        DependencyType.DATABASE
                )
        );

        DependencyGraphEdge edge =
                new DependencyGraphEdge(
                        "application",
                        "postgresql"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> graph.addEdge(edge)
        );
    }

    @Test
    void shouldRejectEdgeWhenTargetNodeDoesNotExist() {

        graph.addNode(
                new DependencyGraphNode(
                        "application",
                        DependencyType.UNKNOWN
                )
        );

        DependencyGraphEdge edge =
                new DependencyGraphEdge(
                        "application",
                        "redis"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> graph.addEdge(edge)
        );
    }

    @Test
    void shouldNotAddDuplicateEdge() {

        graph.addNode(
                new DependencyGraphNode(
                        "application",
                        DependencyType.UNKNOWN
                )
        );

        graph.addNode(
                new DependencyGraphNode(
                        "postgresql",
                        DependencyType.DATABASE
                )
        );

        DependencyGraphEdge edge1 =
                new DependencyGraphEdge(
                        "application",
                        "postgresql"
                );

        DependencyGraphEdge edge2 =
                new DependencyGraphEdge(
                        "application",
                        "postgresql"
                );

        graph.addEdge(edge1);
        graph.addEdge(edge2);

        assertEquals(1, graph.getEdgeCount());
    }

    @Test
    void shouldFindDependentsOfNode() {

        graph.addNode(
                new DependencyGraphNode(
                        "application",
                        DependencyType.UNKNOWN
                )
        );

        graph.addNode(
                new DependencyGraphNode(
                        "postgresql",
                        DependencyType.DATABASE
                )
        );

        graph.addNode(
                new DependencyGraphNode(
                        "redis",
                        DependencyType.CACHE
                )
        );

        graph.addEdge(
                new DependencyGraphEdge(
                        "application",
                        "postgresql"
                )
        );

        graph.addEdge(
                new DependencyGraphEdge(
                        "application",
                        "redis"
                )
        );

        assertEquals(
                1,
                graph.getDependentsOf("postgresql").size()
        );

        assertEquals(
                "application",
                graph.getDependentsOf("postgresql")
                        .get(0)
                        .getSource()
        );
    }

    @Test
    void shouldRemoveNodeAndItsEdges() {

        graph.addNode(
                new DependencyGraphNode(
                        "application",
                        DependencyType.UNKNOWN
                )
        );

        graph.addNode(
                new DependencyGraphNode(
                        "postgresql",
                        DependencyType.DATABASE
                )
        );

        graph.addEdge(
                new DependencyGraphEdge(
                        "application",
                        "postgresql"
                )
        );

        // Two nodes exist before removal.
        assertEquals(2, graph.getNodeCount());
        assertEquals(1, graph.getEdgeCount());

        graph.removeNode("postgresql");

        assertFalse(
                graph.containsNode("postgresql")
        );

        // Application remains.
        assertTrue(
                graph.containsNode("application")
        );

        // PostgreSQL node and its edge are removed.
        assertEquals(1, graph.getNodeCount());
        assertEquals(0, graph.getEdgeCount());
    }

    @Test
    void shouldRemoveSpecificEdge() {

        graph.addNode(
                new DependencyGraphNode(
                        "application",
                        DependencyType.UNKNOWN
                )
        );

        graph.addNode(
                new DependencyGraphNode(
                        "postgresql",
                        DependencyType.DATABASE
                )
        );

        graph.addEdge(
                new DependencyGraphEdge(
                        "application",
                        "postgresql"
                )
        );

        assertEquals(1, graph.getEdgeCount());

        graph.removeEdge(
                "application",
                "postgresql"
        );

        assertEquals(0, graph.getEdgeCount());
    }

    @Test
    void shouldClearEntireGraph() {

        graph.addNode(
                new DependencyGraphNode(
                        "application",
                        DependencyType.UNKNOWN
                )
        );

        graph.addNode(
                new DependencyGraphNode(
                        "postgresql",
                        DependencyType.DATABASE
                )
        );

        graph.addEdge(
                new DependencyGraphEdge(
                        "application",
                        "postgresql"
                )
        );

        graph.clear();

        assertEquals(0, graph.getNodeCount());
        assertEquals(0, graph.getEdgeCount());
        assertTrue(graph.getEdges().isEmpty());
        assertTrue(graph.getNodes().isEmpty());
    }
}
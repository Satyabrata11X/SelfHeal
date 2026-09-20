package com.selfheal.starter.dependency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DependencyGraphServiceTest {

    private DependencyRegistry registry;
    private DependencyGraph graph;
    private DependencyGraphService service;

    @BeforeEach
    void setUp() {

        registry = new DependencyRegistry();

        graph = new DependencyGraph();

        service = new DependencyGraphService(
                registry,
                graph
        );
    }

    @Test
    void shouldCreateDependencyRelationship() {

        Dependency postgres =
                new Dependency(
                        "postgresql",
                        DependencyType.DATABASE
                );

        registry.register(postgres);

        service.addDependencyRelationship(
                "selfheal-demo-component",
                postgres
        );

        assertTrue(
                graph.containsNode(
                        "selfheal-demo-component"
                )
        );

        assertTrue(
                graph.containsNode(
                        "postgresql"
                )
        );

        assertEquals(
                1,
                graph.getEdgeCount()
        );

        assertEquals(
                "postgresql",
                service.getDependenciesOf(
                                "selfheal-demo-component"
                        )
                        .get(0)
                        .getTarget()
        );
    }

    @Test
    void shouldUseExistingDependencyRegistry() {

        Dependency redis =
                new Dependency(
                        "redis",
                        DependencyType.CACHE
                );

        redis.setStatus(
                DependencyStatus.UP
        );

        registry.register(redis);

        assertSame(
                redis,
                service.getDependency("redis")
        );

        assertTrue(
                service.isDependencyHealthy(
                        "redis"
                )
        );
    }

    @Test
    void shouldDetectUnhealthyDependencyFromRegistry() {

        Dependency redis =
                new Dependency(
                        "redis",
                        DependencyType.CACHE
                );

        redis.setStatus(
                DependencyStatus.DOWN
        );

        registry.register(redis);

        assertFalse(
                service.isDependencyHealthy(
                        "redis"
                )
        );
    }

    @Test
    void shouldReturnFalseForUnknownDependency() {

        assertNull(
                service.getDependency(
                        "unknown"
                )
        );

        assertFalse(
                service.isDependencyHealthy(
                        "unknown"
                )
        );
    }

    @Test
    void shouldFindDependentsOfDependency() {

        Dependency postgres =
                new Dependency(
                        "postgresql",
                        DependencyType.DATABASE
                );

        registry.register(postgres);

        service.addDependencyRelationship(
                "selfheal-demo-component",
                postgres
        );

        assertEquals(
                1,
                service.getDependentsOf(
                        "postgresql"
                ).size()
        );

        assertEquals(
                "selfheal-demo-component",
                service.getDependentsOf(
                                "postgresql"
                        )
                        .get(0)
                        .getSource()
        );
    }

    @Test
    void shouldSupportMultipleDependencies() {

        Dependency postgres =
                new Dependency(
                        "postgresql",
                        DependencyType.DATABASE
                );

        Dependency redis =
                new Dependency(
                        "redis",
                        DependencyType.CACHE
                );

        Dependency paymentApi =
                new Dependency(
                        "payment-api",
                        DependencyType.REST_API
                );

        registry.register(postgres);
        registry.register(redis);
        registry.register(paymentApi);

        service.addDependencyRelationship(
                "selfheal-demo-component",
                postgres
        );

        service.addDependencyRelationship(
                "selfheal-demo-component",
                redis
        );

        service.addDependencyRelationship(
                "selfheal-demo-component",
                paymentApi
        );

        assertEquals(
                3,
                service.getDependenciesOf(
                        "selfheal-demo-component"
                ).size()
        );

        assertEquals(
                3,
                graph.getEdgeCount()
        );

        assertEquals(
                4,
                graph.getNodeCount()
        );
    }

    @Test
    void shouldNotCreateDuplicateRelationship() {

        Dependency postgres =
                new Dependency(
                        "postgresql",
                        DependencyType.DATABASE
                );

        registry.register(postgres);

        service.addDependencyRelationship(
                "selfheal-demo-component",
                postgres
        );

        service.addDependencyRelationship(
                "selfheal-demo-component",
                postgres
        );

        assertEquals(
                1,
                graph.getEdgeCount()
        );

        assertEquals(
                1,
                service.getDependenciesOf(
                        "selfheal-demo-component"
                ).size()
        );
    }

    @Test
    void shouldRejectNullDependency() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addDependencyRelationship(
                        "selfheal-demo-component",
                        null
                )
        );
    }

    @Test
    void shouldRejectBlankComponentName() {

        Dependency postgres =
                new Dependency(
                        "postgresql",
                        DependencyType.DATABASE
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addDependencyRelationship(
                        "",
                        postgres
                )
        );
    }

    @Test
    void shouldExposeUnderlyingGraphAndRegistry() {

        assertSame(
                graph,
                service.getGraph()
        );

        assertSame(
                registry,
                service.getRegistry()
        );
    }
}
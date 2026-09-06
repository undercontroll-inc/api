package com.undercontroll.infrastructure.persistence.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderJpaRepositoryTest {

    @Test
    @DisplayName("findDetailById fetches user and items only, not demands")
    void findDetailByIdUsesSingleBag() throws Exception {
        EntityGraph graph = OrderJpaRepository.class
                .getMethod("findDetailById", Integer.class)
                .getAnnotation(EntityGraph.class);

        assertNotNull(graph);
        assertEquals(List.of("user", "orderItems"), List.of(graph.attributePaths()));
    }
}

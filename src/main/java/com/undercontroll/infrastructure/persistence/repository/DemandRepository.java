package com.undercontroll.infrastructure.persistence.repository;

import com.undercontroll.infrastructure.persistence.entity.DemandJpaEntity;
import com.undercontroll.infrastructure.persistence.entity.OrderJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandRepository extends JpaRepository<DemandJpaEntity, Integer> {

    @EntityGraph(attributePaths = {"component", "order"})
    @Query("SELECT d FROM DemandJpaEntity d")
    List<DemandJpaEntity> findAllWithComponent();

    @EntityGraph(attributePaths = {"component", "order"})
    @Query("SELECT d FROM DemandJpaEntity d")
    List<DemandJpaEntity> findAllWithComponent(Pageable pageable);

    @EntityGraph(attributePaths = {"component"})
    List<DemandJpaEntity> findByOrder(OrderJpaEntity order);

    @EntityGraph(attributePaths = {"component", "order"})
    Optional<DemandJpaEntity> findByOrderAndComponent_Id(OrderJpaEntity order, Integer componentId);

    void deleteByOrder(OrderJpaEntity order);
}

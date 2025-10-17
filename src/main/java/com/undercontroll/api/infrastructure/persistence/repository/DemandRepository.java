package com.undercontroll.api.infrastructure.persistence.repository;

import com.undercontroll.api.domain.model.Demand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandRepository extends JpaRepository<Demand, Integer> {
}

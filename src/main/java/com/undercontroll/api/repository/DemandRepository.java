package com.undercontroll.api.repository;

import com.undercontroll.api.model.Demand;
import com.undercontroll.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandRepository extends JpaRepository<Demand, Integer> {
    List<Demand> findByOrder(Order order);
    Optional<Demand> findByOrderAndComponent_Id(Order order, Integer componentId);
    void deleteByOrder(Order order);
}

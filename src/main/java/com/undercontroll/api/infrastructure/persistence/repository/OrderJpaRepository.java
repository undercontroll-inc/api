package com.undercontroll.api.infrastructure.persistence.repository;

import com.undercontroll.api.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Integer> {


}

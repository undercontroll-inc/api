package com.undercontroll.api.infrastructure.persistence.repository;

import com.undercontroll.api.domain.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Integer> {
}

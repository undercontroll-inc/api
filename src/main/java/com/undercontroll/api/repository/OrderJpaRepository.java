package com.undercontroll.api.repository;

import com.undercontroll.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Integer> {


}

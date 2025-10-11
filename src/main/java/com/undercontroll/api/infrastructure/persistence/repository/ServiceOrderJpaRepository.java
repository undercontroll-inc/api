package com.undercontroll.api.infrastructure.persistence.repository;

import com.undercontroll.api.domain.model.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOrderJpaRepository extends JpaRepository <ServiceOrder, Integer> {
    List<ServiceOrder> findByOrderId(Integer orderId);
}
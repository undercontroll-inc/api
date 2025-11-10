package com.undercontroll.api.repository;

import com.undercontroll.api.model.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOrderJpaRepository extends JpaRepository <ServiceOrder, Integer> {
    List<ServiceOrder> findByOrder_Id(Integer orderId);
}
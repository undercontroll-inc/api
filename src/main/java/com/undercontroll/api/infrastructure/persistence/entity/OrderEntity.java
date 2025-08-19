package com.undercontroll.api.infrastructure.persistence.entity;

import com.undercontroll.api.domain.model.OrderItem;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private List<OrderItem> orderItems;

    private LocalDateTime startedAt;

    private LocalDateTime completedTime;

}

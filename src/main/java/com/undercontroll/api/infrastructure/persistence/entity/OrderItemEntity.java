package com.undercontroll.api.infrastructure.persistence.entity;

import com.undercontroll.api.domain.enums.OrderItemStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_itens")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String imageUrl;

    private Double price;

    private Double discount;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    private LocalDateTime sentAt;

    private LocalDateTime requestedAt;

    private LocalDateTime lastReview;

    private LocalDateTime analyzedAt;

    private LocalDateTime completedAt;

    private LocalDateTime payedAt;

}

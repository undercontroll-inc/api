package com.undercontroll.api.domain.model;

import com.undercontroll.api.domain.enums.OrderItemStatus;

import java.time.LocalDateTime;

public class OrderItem {

    private Integer id;
    private String name;
    private String imageUrl;
    private Double price;
    private Double discount;
    private Integer quantity;
    private OrderItemStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime requestedAt;
    private LocalDateTime lastReview;
    private LocalDateTime analyzedAt;
    private LocalDateTime completedAt;
    private LocalDateTime payedAt;


}

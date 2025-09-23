package com.undercontroll.api.domain.model;

import com.undercontroll.api.domain.enums.OrderItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

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

    public OrderItem(String name, String imageUrl, Order order, Double price, Double discount,
                     Integer quantity, OrderItemStatus status, LocalDateTime sentAt, LocalDateTime requestedAt,
                     LocalDateTime lastReview, LocalDateTime analyzedAt, LocalDateTime completedAt, LocalDateTime payedAt) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.order = order;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
        this.status = status;
        this.sentAt = sentAt;
        this.requestedAt = requestedAt;
        this.lastReview = lastReview;
        this.analyzedAt = analyzedAt;
        this.completedAt = completedAt;
        this.payedAt = payedAt;
    }
}

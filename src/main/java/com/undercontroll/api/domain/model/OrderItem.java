package com.undercontroll.api.domain.model;

import com.undercontroll.api.domain.enums.OrderItemStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
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

    public OrderItem() {
    }

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


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getLastReview() {
        return lastReview;
    }

    public void setLastReview(LocalDateTime lastReview) {
        this.lastReview = lastReview;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getPayedAt() {
        return payedAt;
    }

    public void setPayedAt(LocalDateTime payedAt) {
        this.payedAt = payedAt;
    }
}

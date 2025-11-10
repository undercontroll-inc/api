package com.undercontroll.api.model;

import com.undercontroll.api.model.OrderItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String imageUrl;

    private Double labor;
    private String observation;
    private String volt;
    private String series;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    private LocalDateTime lastReview;
    private LocalDateTime analyzedAt;
    private LocalDateTime completedAt;

    @OneToMany(fetch =  FetchType.LAZY)
    private List<Demand> demands;

}

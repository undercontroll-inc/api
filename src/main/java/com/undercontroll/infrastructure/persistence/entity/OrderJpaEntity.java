package com.undercontroll.infrastructure.persistence.entity;

import com.undercontroll.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "`order`")
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    @Builder.Default
    private List<OrderItemJpaEntity> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DemandJpaEntity> demands = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private UserJpaEntity user;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Double total;
    private Double discount;

    private boolean fabricGuarantee;
    private boolean returnGuarantee;
    private String description;
    private String nf;
    private Date date;
    private String store;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDate received_at;

    private LocalDate completedTime;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void addOrderItem(OrderItemJpaEntity orderItem) {
        this.orderItems.add(orderItem);
    }

    public void removeOrderItem(OrderItemJpaEntity orderItem) {
        this.orderItems.remove(orderItem);
    }
}

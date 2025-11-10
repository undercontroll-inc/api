package com.undercontroll.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_order")
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    private Integer fabricGuarantee;
    private Integer returnGuarantee;
    private String description;
    private String nf;
    private Date date;
    private String store;
    private String issue;
    private LocalDateTime withdraw_at;
    private LocalDateTime received_at;

}

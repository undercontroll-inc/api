package com.undercontroll.api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
public class ServiceOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer serviceOrderId;

    @ManyToOne
    private User user;

    @OneToMany
    private List<ComponentPart> componentPartList;

    @ManyToOne
    private Order order;

    private Integer fabricGuarantee;

    private Integer budget;

    private Integer returnGuarantee;

    private String description;

    private String nf;

    private Date date;

    private String store;

    private String issue;
}

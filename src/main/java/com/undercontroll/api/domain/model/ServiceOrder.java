package com.undercontroll.api.domain.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
public class ServiceOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer serviceOrderId;

    private User user;

    private List<ComponentPart> componentPartList;

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

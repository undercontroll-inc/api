package com.undercontroll.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer serviceOrderId;


    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_user_id")
    private User user;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "service_order_components",
            joinColumns = @JoinColumn(name = "service_order_id"),
            inverseJoinColumns = @JoinColumn(name = "component_id")
    )
    private List<ComponentPart> componentPartList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
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

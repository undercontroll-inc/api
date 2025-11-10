package com.undercontroll.api.model;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "component")
public class ComponentPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    private String brand;

    private Double price;

    private String supplier;

    private String category;

    private Long quantity;

}

package com.undercontroll.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "demand")
public class Demand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    private ComponentPart component;

}

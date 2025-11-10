package com.undercontroll.api.model;

import com.undercontroll.api.model.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String lastName;

    private String email;

    private String password;

    private String address;

    private String cpf;

    @Length(min = 8, max = 8)
    private String CEP;

    private String phone;

    private String avatarUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private UserType userType;

}
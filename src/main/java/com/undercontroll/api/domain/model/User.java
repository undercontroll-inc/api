package com.undercontroll.api.domain.model;

import com.undercontroll.api.domain.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Setter
    private String name;

    @Setter
    private String lastName;

    @Setter
    private String email;

    @Setter
    private String password;

    @Setter
    private String address;

    @Setter
    private String cpf;

    @Setter
    private Date birthDate;

    @Setter
    @Enumerated(EnumType.STRING)
    private UserType userType;

}

package com.undercontroll.api.domain.model;

import com.undercontroll.api.domain.enums.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
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

    public User(String name, String lastName, String password, String address, String cpf, Date birthDate, UserType userType) {
        this.name = name;
        this.lastName = lastName;
        this.password = password;
        this.address = address;
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.userType = userType;
    }
}

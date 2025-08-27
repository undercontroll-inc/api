package com.undercontroll.api.domain.model;

import com.undercontroll.api.domain.enums.UserType;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String name;
    private String lastName;
    private String password;
    private String address;
    private String cpf;
    private Date birthDate;

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

    public User() {}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getLastName() {return lastName;}

    public void setLastName(String lastName) {this.lastName = lastName;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

    public String getCpf() {return cpf;}

    public void setCpf(String cpf) {this.cpf = cpf;}

    public Date getBirthDate() {return birthDate;}

    public void setBirthDate(Date birthDate) {this.birthDate = birthDate;}

    public UserType getUserType() {return userType;}

    public void setUserType(UserType userType) {this.userType = userType;}
}

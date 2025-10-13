package com.undercontroll.api.application.dto;

import com.undercontroll.api.domain.enums.UserType;

import java.util.Date;

public record CreateUserRequest(
        String name,
        String email,
        String phone,
        String lastName,
        String password,
        String address,
        String cpf,
        String avatarUrl,
        UserType userType,
        String CEP
){
}

package com.undercontroll.api.dto;

import com.undercontroll.api.model.enums.UserType;

public record UpdateUserRequest(
        Integer id,
        String name,
        String lastName,
        String password,
        String address,
        String cpf,
        String phone,
        String avatarUrl,
        String CEP,
        UserType userType
) {}

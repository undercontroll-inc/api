package com.undercontroll.api.application.dto;

import com.undercontroll.api.domain.enums.UserType;

public record UserDto (
     String name,
     String email,
     String lastName,
     String address,
     String cpf,
     String CEP,
     String phone,
     String avatarUrl,
     UserType userType
) {}

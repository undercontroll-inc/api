package com.undercontroll.api.dto;

import com.undercontroll.api.model.UserType;

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

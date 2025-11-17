package com.undercontroll.api.dto;

import com.undercontroll.api.model.enums.UserType;

public record UserDto (
     Integer id,
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

package com.undercontroll.api.application.dto;

import com.undercontroll.api.domain.enums.UserType;
import java.util.Date;

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

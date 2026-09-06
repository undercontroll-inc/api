package com.undercontroll.application.dto.user;

import com.undercontroll.domain.enums.UserType;

public record UpdateUserRequest(
        String name,
        String lastName,
        String password,
        String address,
        String cpf,
        String phone,
        String avatarUrl,
        String CEP,
        Boolean alreadyRecurrent,
        Boolean hasWhatsApp,
        Boolean inFirstLogin,
        UserType userType
) {}

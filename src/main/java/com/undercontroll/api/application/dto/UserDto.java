package com.undercontroll.api.application.dto;

import com.undercontroll.api.domain.enums.UserType;
import java.util.Date;

public record UserDto (
     String name,
     String lastName,
     String password,
     String address,
     String cpf,
     Date birthDate,
     UserType userType
) {}




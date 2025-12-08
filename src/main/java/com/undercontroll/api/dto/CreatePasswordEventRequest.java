package com.undercontroll.api.dto;

import com.undercontroll.api.model.enums.PasswordEventType;

public record CreatePasswordEventRequest(
        PasswordEventType type,
        String agent,
        String userPhone,
        String value
) {
}

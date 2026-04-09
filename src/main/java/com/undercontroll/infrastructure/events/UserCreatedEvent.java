package com.undercontroll.infrastructure.events;

import java.time.LocalDateTime;

public record UserCreatedEvent(
        String name,
        String email,
        LocalDateTime createdAt
) {
}

package com.undercontroll.application.dto.order;

import java.time.LocalDateTime;
import java.util.Optional;

public record GetOrderByDateRequest(
        Optional<LocalDateTime> startedDate,
        Optional<LocalDateTime> completedDate
) {
}

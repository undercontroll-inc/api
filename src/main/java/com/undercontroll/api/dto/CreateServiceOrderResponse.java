package com.undercontroll.api.dto;

import com.undercontroll.api.model.ComponentPart;
import com.undercontroll.api.model.Order;
import com.undercontroll.api.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Builder
public record CreateServiceOrderResponse(
        @NotNull
        Order order,

        @NotNull
        @PositiveOrZero
        boolean fabricGuarantee,


        @NotNull
        @PositiveOrZero
        boolean returnGuarantee,

        @NotBlank
        String description,

        @NotBlank
        String nf,

        @NotNull
        Date date,

        @NotBlank
        String store,

        @NotBlank
        String issue,

        LocalDateTime withdrawAt,
        LocalDateTime receivedAt

) {
}

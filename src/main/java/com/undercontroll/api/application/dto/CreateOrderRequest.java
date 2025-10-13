package com.undercontroll.api.application.dto;

import jakarta.annotation.Nullable;

import java.util.List;

public record CreateOrderRequest(

        @Nullable
        List<Integer> orderItemIds

) {
}

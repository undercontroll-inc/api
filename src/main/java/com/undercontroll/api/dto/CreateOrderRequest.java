package com.undercontroll.api.dto;

import jakarta.annotation.Nullable;

import java.util.List;

public record CreateOrderRequest(

        @Nullable
        List<Integer> orderItemIds

) {
}

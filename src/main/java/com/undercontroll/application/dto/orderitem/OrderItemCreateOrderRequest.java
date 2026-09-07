package com.undercontroll.application.dto.orderitem;

public record OrderItemCreateOrderRequest(
        String type,
        String brand,
        String model,
        String voltage,
        String serial,
        Double laborValue
) {
}

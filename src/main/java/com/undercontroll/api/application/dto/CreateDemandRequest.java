package com.undercontroll.api.application.dto;

public record CreateDemandRequest(
        Integer componentId,
        Integer orderItemId,
        Integer quantity
) {
}

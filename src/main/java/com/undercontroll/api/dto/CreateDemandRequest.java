package com.undercontroll.api.dto;

public record CreateDemandRequest(
        Integer componentId,
        Integer orderItemId,
        Integer quantity
) {
}

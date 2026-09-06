package com.undercontroll.application.dto.demand;

public record CreateDemandRequest(
        Integer componentPartId,
        Long quantity
) {
}

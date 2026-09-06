package com.undercontroll.application.dto.demand;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Demand representation")
public record DemandDto(
        @Schema(description = "Demand id", example = "25")
        Integer id,
        @Schema(description = "Component id", example = "10")
        Integer componentId,
        @Schema(description = "Order id", example = "5")
        Integer orderId,
        @Schema(description = "Requested quantity", example = "3")
        Long quantity
) {
}

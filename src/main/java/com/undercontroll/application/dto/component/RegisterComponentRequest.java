package com.undercontroll.application.dto.component;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to create a new component")
public record RegisterComponentRequest(
        @Schema(description = "Component name", example = "Resistor 10k Ohm")
        String item,

        @Schema(description = "Detailed component description", example = "Precision resistor 10k Ohm 1% 1/4W")
        String description,

        @Schema(description = "Component brand/manufacturer", example = "Vishay")
        String brand,

        @Schema(description = "Component category", example = "Electronics")
        String category,

        @Schema(description = "Stock quantity", example = "100")
        Integer quantity,

        @Schema(description = "Unit price", example = "1.50")
        Double price,

        @Schema(description = "Component supplier", example = "Mouser Electronics")
        String supplier
) {
}

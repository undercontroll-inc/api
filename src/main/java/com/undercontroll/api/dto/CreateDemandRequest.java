package com.undercontroll.api.dto;

import com.undercontroll.api.model.ComponentPart;
import com.undercontroll.api.model.Order;

public record CreateDemandRequest(
        ComponentPart componentPart,
        Long quantity,
        Order order
) {
}

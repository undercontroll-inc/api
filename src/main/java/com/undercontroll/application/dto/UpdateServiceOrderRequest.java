package com.undercontroll.application.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Date;
import java.util.List;

public record UpdateServiceOrderRequest(

        @Id
        @NotNull
        @Positive
        Integer serviceOrderId,

        UserDto user,
        List<ComponentDto> componentPartList,
        OrderDto order,
        boolean fabricGuarantee,
        Integer budget,
        boolean returnGuarantee,
        String description,
        String nf,
        Date date,
        String store,
        String issue

) {
}

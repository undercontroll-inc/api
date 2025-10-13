package com.undercontroll.api.application.dto;

import com.undercontroll.api.domain.model.ComponentPart;
import com.undercontroll.api.domain.model.Order;
import com.undercontroll.api.domain.model.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Builder
public record ServiceOrderDto (

        Integer fabricGuarantee,
        Integer returnGuarantee,
        String description,
        String nf,
        Date date,
        String store,
        String issue,
        LocalDateTime withDrawAt,
        LocalDateTime receivedAt
){
}

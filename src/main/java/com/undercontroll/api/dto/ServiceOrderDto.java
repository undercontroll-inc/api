package com.undercontroll.api.dto;

import com.undercontroll.api.model.ComponentPart;
import com.undercontroll.api.model.Order;
import com.undercontroll.api.model.User;
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

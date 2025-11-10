package com.undercontroll.api.dto;

import java.time.LocalDateTime;
import java.util.Date;

public record CreateServiceOrderRequest(

        Integer orderId,
        Integer fabricGuarantee,
        Integer budget,
        Integer returnGuarantee,
        String description,
        String nf,
        Date date,
        String store,
        String issue,
        LocalDateTime withdrawalAt,
        LocalDateTime received_at

) {
}

package com.undercontroll.api.application.dto;

import com.undercontroll.api.domain.model.ComponentPart;
import com.undercontroll.api.domain.model.Order;
import com.undercontroll.api.domain.model.User;

import java.util.Date;
import java.util.List;

public record ServiceOrderDto (
        User user,
        List<ComponentPart> componentPartList,
        Order order,
        Integer fabricGuarantee,
        Integer budget,
        Integer returnGuarantee,
        String description,
        String nf,
        Date date,
        String store,
        String issue

){
}

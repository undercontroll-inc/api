package com.undercontroll.domain.usecase.demand;

import com.undercontroll.application.dto.demand.DemandDto;

import java.util.List;

public interface GetDemandsPort {
    List<DemandDto> execute(Integer orderId, Integer componentId);
}

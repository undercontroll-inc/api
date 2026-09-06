package com.undercontroll.domain.usecase.demand;

import com.undercontroll.application.dto.demand.CreateDemandRequest;
import com.undercontroll.application.dto.demand.DemandDto;

public interface CreateDemandPort {
    DemandDto execute(Integer orderId, CreateDemandRequest request);
}

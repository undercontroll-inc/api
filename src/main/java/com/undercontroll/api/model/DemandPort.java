package com.undercontroll.api.model;

import com.undercontroll.api.dto.CreateDemandRequest;

public interface DemandPort {

    void createDemand(
            CreateDemandRequest createDemandRequest
    );

}

package com.undercontroll.api.application.port;

import com.undercontroll.api.application.dto.CreateDemandRequest;

public interface DemandPort {

    void createDemand(
            CreateDemandRequest createDemandRequest
    );

}

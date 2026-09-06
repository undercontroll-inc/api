package com.undercontroll.domain.usecase.dashboard;

import com.undercontroll.domain.enums.PeriodFilter;
import com.undercontroll.domain.enums.StatusFilter;
import com.undercontroll.application.dto.dashboard.TopComponentsResponse;

public interface GetTopComponentsPort {
    TopComponentsResponse execute(PeriodFilter period, StatusFilter status);
}

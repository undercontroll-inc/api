package com.undercontroll.domain.usecase.dashboard;

import com.undercontroll.domain.enums.PeriodFilter;
import com.undercontroll.domain.enums.StatusFilter;
import com.undercontroll.application.dto.dashboard.TopAppliancesResponse;

public interface GetTopAppliancesPort {
    TopAppliancesResponse execute(PeriodFilter period, StatusFilter status);
}

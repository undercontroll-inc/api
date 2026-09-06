package com.undercontroll.domain.usecase.dashboard;

import com.undercontroll.application.dto.dashboard.DashboardMetricsResponse;
import com.undercontroll.domain.enums.PeriodFilter;

public interface GetOngoingOrdersPort {
    DashboardMetricsResponse execute(PeriodFilter period);
}

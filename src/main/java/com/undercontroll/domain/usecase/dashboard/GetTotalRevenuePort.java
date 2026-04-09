package com.undercontroll.domain.usecase.dashboard;

import com.undercontroll.domain.enums.PeriodFilter;
import com.undercontroll.domain.enums.StatusFilter;

public interface GetTotalRevenuePort {
    record Input(
            PeriodFilter period,
            StatusFilter status
    ) {}

    record Output(
            Double totalRevenue
    ) {}

    Output execute(Input input);
}

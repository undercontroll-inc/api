package com.undercontroll.domain.usecase.dashboard;

import com.undercontroll.domain.enums.PeriodFilter;
import com.undercontroll.domain.enums.StatusFilter;

public interface GetAverageOrderPricePort {
    record Input(
            PeriodFilter period,
            StatusFilter status
    ) {}

    record Output(
            Double averageOrderPrice
    ) {}

    Output execute(Input input);
}

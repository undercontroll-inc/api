package com.undercontroll.domain.usecase.dashboard;

import com.undercontroll.domain.enums.PeriodFilter;
import com.undercontroll.domain.enums.StatusFilter;
import com.undercontroll.application.dto.dashboard.CustomerTypeResponse;

public interface GetCustomerTypeEvolutionPort {
    CustomerTypeResponse execute(PeriodFilter period, StatusFilter status);
}

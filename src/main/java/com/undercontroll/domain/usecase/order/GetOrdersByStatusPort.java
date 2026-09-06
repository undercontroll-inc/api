package com.undercontroll.domain.usecase.order;

import com.undercontroll.domain.enums.PeriodFilter;
import com.undercontroll.application.dto.dashboard.OrdersByStatusResponse;

public interface GetOrdersByStatusPort {
    OrdersByStatusResponse execute(PeriodFilter period);
}

package com.undercontroll.domain.usecase.order.impl;

import com.undercontroll.application.dto.dashboard.OrdersByStatusResponse;
import com.undercontroll.domain.enums.OrderStatus;
import com.undercontroll.domain.enums.PeriodFilter;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.domain.usecase.dashboard.DashboardDateFilter;
import com.undercontroll.domain.usecase.order.GetOrdersByStatusPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetOrdersByStatusImpl implements GetOrdersByStatusPort {

    private final OrderGateway orderGateway;

    @Override
    public OrdersByStatusResponse execute(PeriodFilter period) {
        LocalDate startDate = DashboardDateFilter.from(period);

        List<Object[]> rows = orderGateway.getOrdersByStatus(startDate);

        List<OrdersByStatusResponse.StatusCount> statusCounts = rows.stream()
                .map(row -> new OrdersByStatusResponse.StatusCount(
                        OrderStatus.valueOf((String) row[0]),
                        toLong(row[1])
                ))
                .toList();

        return new OrdersByStatusResponse(statusCounts);
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long l) return l;
        if (value instanceof Number n) return n.longValue();
        return 0L;
    }
}

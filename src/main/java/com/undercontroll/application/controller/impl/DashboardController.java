package com.undercontroll.application.controller.impl;

import com.undercontroll.domain.usecase.dashboard.*;
import com.undercontroll.domain.usecase.order.GetOrdersByStatusPort;
import com.undercontroll.application.controller.DashboardApi;
import com.undercontroll.application.dto.dashboard.CustomerTypeResponse;
import com.undercontroll.application.dto.dashboard.DashboardMetricsResponse;
import com.undercontroll.application.dto.dashboard.OrdersByStatusResponse;
import com.undercontroll.application.dto.dashboard.RevenueEvolutionResponse;
import com.undercontroll.application.dto.dashboard.TopAppliancesResponse;
import com.undercontroll.application.dto.dashboard.TopComponentsResponse;
import com.undercontroll.domain.enums.PeriodFilter;
import com.undercontroll.domain.enums.StatusFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController implements DashboardApi {

    private final GetTotalRevenuePort getTotalRevenuePort;
    private final GetProfitMarginPort getProfitMarginPort;
    private final GetAverageOrderPricePort getAverageOrderPricePort;
    private final GetOngoingOrdersPort getOngoingOrdersPort;
    private final GetAverageRepairTimePort getAverageRepairTimePort;
    private final GetRevenueEvolutionPort getRevenueEvolutionPort;
    private final GetCustomerTypeEvolutionPort getCustomerTypeEvolutionPort;
    private final GetOrdersByStatusPort getOrdersByStatusPort;
    private final GetTopAppliancesPort getTopAppliancesPort;
    private final GetTopComponentsPort getTopComponentsPort;

    @Override
    public ResponseEntity<DashboardMetricsResponse> getDashboardMetrics(PeriodFilter period, StatusFilter status) {
        return ResponseEntity.ok(getTotalRevenuePort.execute(period, status));
    }

    @Override
    public ResponseEntity<DashboardMetricsResponse> getProfitMargin(PeriodFilter period, StatusFilter status) {
        return ResponseEntity.ok(getProfitMarginPort.execute(period, status));
    }

    @Override
    public ResponseEntity<DashboardMetricsResponse> getAverageOrderPrice(PeriodFilter period, StatusFilter status) {
        return ResponseEntity.ok(getAverageOrderPricePort.execute(period, status));
    }

    @Override
    public ResponseEntity<DashboardMetricsResponse> getOngoingOrders(PeriodFilter period) {
        return ResponseEntity.ok(getOngoingOrdersPort.execute(period));
    }

    @Override
    public ResponseEntity<DashboardMetricsResponse> getAverageRepairTime(PeriodFilter period, StatusFilter status) {
        return ResponseEntity.ok(getAverageRepairTimePort.execute(period, status));
    }

    @Override
    public ResponseEntity<RevenueEvolutionResponse> getRevenueEvolution(PeriodFilter period, StatusFilter status) {
        return ResponseEntity.ok(getRevenueEvolutionPort.execute(period, status));
    }

    @Override
    public ResponseEntity<CustomerTypeResponse> getCustomerTypeEvolution(PeriodFilter period, StatusFilter status) {
        return ResponseEntity.ok(getCustomerTypeEvolutionPort.execute(period, status));
    }

    @Override
    public ResponseEntity<OrdersByStatusResponse> getOrdersByStatus(PeriodFilter period) {
        return ResponseEntity.ok(getOrdersByStatusPort.execute(period));
    }

    @Override
    public ResponseEntity<TopAppliancesResponse> getTopAppliances(PeriodFilter period, StatusFilter status) {
        return ResponseEntity.ok(getTopAppliancesPort.execute(period, status));
    }

    @Override
    public ResponseEntity<TopComponentsResponse> getTopComponents(PeriodFilter period, StatusFilter status) {
        return ResponseEntity.ok(getTopComponentsPort.execute(period, status));
    }
}

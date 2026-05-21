package com.undercontroll.domain.gateway;

import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.model.PaginatedResult;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderGateway {

    Order save(Order order);

    void deleteById(Integer id);

    Optional<Order> findById(Integer id);

    List<Order> findAll();

    PaginatedResult<Order> findAllPaginated(Integer offset, Integer limit);

    List<Order> findByUserId(Integer userId);

    Optional<Order> findOrderByOrderItemId(Integer orderItemId);

    Double calculatePartsTotalByOrderId(Integer orderId);

    List<Object[]> findAllPartsByOrderIdNative(Integer orderId);

    Double calculateTotalRevenueFiltered(LocalDate startDate, List<String> statuses);

    Double calculateTotalPartsCostFiltered(LocalDate startDate, List<String> statuses);

    Double calculateAverageOrderPriceFiltered(LocalDate startDate, List<String> statuses);

    Long countOngoingOrdersFiltered(LocalDate startDate, List<String> statuses);

    Double calculateAverageRepairTimeFiltered(LocalDate startDate, List<String> statuses);

    List<Object[]> getRevenueEvolution(LocalDate startDate, List<String> statuses);

    List<Object[]> getCustomerTypeEvolution(LocalDate startDate, List<String> statuses);

    List<Object[]> getOrdersByStatus(LocalDate startDate);

    List<Object[]> getTopAppliances(LocalDate startDate, List<String> statuses);

    List<Object[]> getTopComponents(LocalDate startDate, List<String> statuses);

}

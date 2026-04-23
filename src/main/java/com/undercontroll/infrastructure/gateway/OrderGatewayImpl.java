package com.undercontroll.infrastructure.gateway;

import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.model.PaginatedResult;
import com.undercontroll.domain.enums.OrderStatus;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.infrastructure.mapper.OrderMapper;
import com.undercontroll.infrastructure.persistence.entity.OrderJpaEntity;
import com.undercontroll.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderGatewayImpl implements OrderGateway {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public Order save(Order order) {
        OrderJpaEntity jpaEntity = orderMapper.toEntityWithId(order);
        OrderJpaEntity savedEntity = orderJpaRepository.save(jpaEntity);
        return orderMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Integer id) {
        orderJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Order> findById(Integer id) {
        return orderJpaRepository.findById(id).map(orderMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll().stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public PaginatedResult<Order> findAllPaginated(Integer offset, Integer limit) {
        Page<OrderJpaEntity> pageResult = orderJpaRepository.findAllPaginated(PageRequest.of(offset, limit));
        List<Order> content = pageResult.getContent().stream()
                .map(orderMapper::toDomain)
                .toList();
        return new PaginatedResult<>(content, pageResult.getTotalElements());
    }

    @Override
    public List<Order> findByUserId(Integer userId) {
        return orderJpaRepository.findByUser_id(userId).stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Order> findOrderByOrderItemId(Integer orderItemId) {
        return orderJpaRepository.findOrderByOrderItemId(orderItemId)
                .map(orderMapper::toDomain);
    }

    @Override
    public Double calculatePartsTotalByOrderId(Integer orderId) {
        return orderJpaRepository.calculatePartsTotalByOrderId(orderId);
    }

    @Override
    public List<Object[]> findAllPartsByOrderIdNative(Integer orderId) {
        return orderJpaRepository.findAllPartsByOrderIdNative(orderId);
    }

    @Override
    public Double calculateTotalRevenueFiltered(LocalDate startDate, List<OrderStatus> statuses) {
        return orderJpaRepository.calculateTotalRevenueFiltered(startDate, statuses);
    }

    @Override
    public Double calculateTotalPartsCostFiltered(LocalDate startDate, List<String> statuses) {
        return orderJpaRepository.calculateTotalPartsCostFiltered(startDate, statuses);
    }

    @Override
    public Double calculateAverageOrderPriceFiltered(LocalDate startDate, List<String> statuses) {
        List<OrderStatus> orderStatuses = statuses.stream()
                .map(OrderStatus::valueOf)
                .toList();
        return orderJpaRepository.calculateAverageOrderPriceFiltered(startDate, orderStatuses);
    }

    @Override
    public Long countOngoingOrdersFiltered(LocalDate startDate, List<String> statuses) {
        List<OrderStatus> orderStatuses = statuses.stream()
                .map(OrderStatus::valueOf)
                .toList();
        return orderJpaRepository.countOngoingOrdersFiltered(startDate, orderStatuses);
    }

    @Override
    public Double calculateAverageRepairTimeFiltered(LocalDate startDate, List<String> statuses) {
        List<OrderStatus> orderStatuses = statuses.stream()
                .map(OrderStatus::valueOf)
                .toList();
        return orderJpaRepository.calculateAverageRepairTimeFiltered(startDate, orderStatuses);
    }

    @Override
    public List<Object[]> getRevenueEvolution(LocalDate startDate, List<String> statuses) {
        return orderJpaRepository.getRevenueEvolution(startDate, statuses);
    }

    @Override
    public List<Object[]> getCustomerTypeEvolution(LocalDate startDate, List<String> statuses) {
        return orderJpaRepository.getCustomerTypeEvolution(startDate, statuses);
    }

    @Override
    public List<Object[]> getOrdersByStatus(LocalDate startDate) {
        return orderJpaRepository.getOrdersByStatus(startDate);
    }

    @Override
    public List<Object[]> getTopAppliances(LocalDate startDate, List<String> statuses) {
        return orderJpaRepository.getTopAppliances(startDate, statuses);
    }

    @Override
    public List<Object[]> getTopComponents(LocalDate startDate, List<String> statuses) {
        return orderJpaRepository.getTopComponents(startDate, statuses);
    }

}

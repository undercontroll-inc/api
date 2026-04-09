package com.undercontroll.infrastructure.gateway;

import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.domain.gateway.OrderItemGateway;
import com.undercontroll.infrastructure.mapper.OrderItemMapper;
import com.undercontroll.infrastructure.persistence.entity.OrderItemJpaEntity;
import com.undercontroll.infrastructure.persistence.repository.OrderItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderItemGatewayImpl implements OrderItemGateway {

    private final OrderItemJpaRepository orderItemJpaRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderItem save(OrderItem orderItem) {
        OrderItemJpaEntity jpaEntity = orderItemMapper.toEntityWithId(orderItem);
        OrderItemJpaEntity savedEntity = orderItemJpaRepository.save(jpaEntity);
        return orderItemMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Integer id) {
        orderItemJpaRepository.deleteById(id);
    }

    @Override
    public Optional<OrderItem> findById(Integer id) {
        return orderItemJpaRepository.findById(id).map(orderItemMapper::toDomain);
    }

    @Override
    public List<OrderItem> findAll() {
        return orderItemJpaRepository.findAll().stream()
                .map(orderItemMapper::toDomain)
                .toList();
    }

}

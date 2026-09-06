package com.undercontroll.infrastructure.gateway;

import com.undercontroll.domain.model.Demand;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.gateway.DemandGateway;
import com.undercontroll.infrastructure.mapper.DemandMapper;
import com.undercontroll.infrastructure.mapper.OrderMapper;
import com.undercontroll.infrastructure.persistence.entity.DemandJpaEntity;
import com.undercontroll.infrastructure.persistence.entity.OrderJpaEntity;
import com.undercontroll.infrastructure.persistence.repository.DemandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DemandGatewayImpl implements DemandGateway {

    private final DemandRepository demandRepository;
    private final DemandMapper demandMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public Demand save(Demand demand) {
        DemandJpaEntity jpaEntity = demandMapper.toEntityWithId(demand);
        DemandJpaEntity savedEntity = demandRepository.save(jpaEntity);
        return demandMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        demandRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByOrder(Order order) {
        OrderJpaEntity orderJpaEntity = orderMapper.toEntityWithId(order);
        demandRepository.deleteByOrder(orderJpaEntity);
    }

    @Override
    public Optional<Demand> findById(Integer id) {
        return demandRepository.findById(id).map(demandMapper::toDomain);
    }

    @Override
    public List<Demand> findAll() {
        return demandRepository.findAllWithComponent().stream()
                .map(demandMapper::toDomain)
                .toList();
    }

    @Override
    public List<Demand> findRecent(int limit) {
        return demandRepository.findAllWithComponent(PageRequest.of(0, Math.max(1, limit))).stream()
                .map(demandMapper::toDomain)
                .toList();
    }

    @Override
    public List<Demand> findByOrder(Order order) {
        OrderJpaEntity orderJpaEntity = orderMapper.toEntityWithId(order);
        return demandRepository.findByOrder(orderJpaEntity).stream()
                .map(demandMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Demand> findByOrderAndComponentId(Order order, Integer componentId) {
        OrderJpaEntity orderJpaEntity = orderMapper.toEntityWithId(order);
        return demandRepository.findByOrderAndComponent_Id(orderJpaEntity, componentId)
                .map(demandMapper::toDomain);
    }

}

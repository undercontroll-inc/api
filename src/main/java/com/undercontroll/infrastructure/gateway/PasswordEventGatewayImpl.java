package com.undercontroll.infrastructure.gateway;

import com.undercontroll.domain.model.PasswordEvent;
import com.undercontroll.domain.enums.PasswordEventStatus;
import com.undercontroll.domain.enums.PasswordEventType;
import com.undercontroll.infrastructure.mapper.PasswordEventMapper;
import com.undercontroll.infrastructure.persistence.entity.PasswordEventJpaEntity;
import com.undercontroll.infrastructure.persistence.repository.PasswordEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PasswordEventGatewayImpl implements com.undercontroll.domain.gateway.PasswordEventGateway {

    private final PasswordEventRepository passwordEventRepository;
    private final PasswordEventMapper passwordEventMapper;

    @Override
    public PasswordEvent save(PasswordEvent passwordEvent) {
        PasswordEventJpaEntity jpaEntity = passwordEventMapper.toEntityWithId(passwordEvent);
        PasswordEventJpaEntity savedEntity = passwordEventRepository.save(jpaEntity);
        return passwordEventMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(UUID id) {
        passwordEventRepository.deleteById(id);
    }

    @Override
    public Optional<PasswordEvent> findById(UUID id) {
        return passwordEventRepository.findById(id).map(passwordEventMapper::toDomain);
    }

    @Override
    public List<PasswordEvent> findAll() {
        return passwordEventRepository.findAll().stream()
                .map(passwordEventMapper::toDomain)
                .toList();
    }

    @Override
    public List<PasswordEvent> findByUserPhone(String userPhone) {
        return passwordEventRepository.findByUserPhone(userPhone).stream()
                .map(passwordEventMapper::toDomain)
                .toList();
    }

    @Override
    public List<PasswordEvent> findByCreatedAtBetweenAndType(LocalDateTime start, LocalDateTime end, PasswordEventType type) {
        return passwordEventRepository.findByCreatedAtBetweenAndType(start, end, type).stream()
                .map(passwordEventMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<PasswordEvent> findByStatusAndType(PasswordEventStatus status, PasswordEventType type) {
        return Optional.ofNullable(passwordEventRepository.findByStatusAndType(status, type))
                .map(passwordEventMapper::toDomain);
    }

}

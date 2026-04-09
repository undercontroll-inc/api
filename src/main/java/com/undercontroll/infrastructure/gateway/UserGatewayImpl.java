package com.undercontroll.infrastructure.gateway;

import com.undercontroll.domain.model.User;
import com.undercontroll.domain.gateway.UserGateway;
import com.undercontroll.infrastructure.mapper.UserMapper;
import com.undercontroll.infrastructure.persistence.entity.UserJpaEntity;
import com.undercontroll.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserGatewayImpl implements UserGateway {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity = userMapper.toEntityWithId(user);
        UserJpaEntity savedEntity = userJpaRepository.save(jpaEntity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Integer id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userJpaRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findUserByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userJpaRepository.findUserByPhone(phone).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByCpf(String cpf) {
        return userJpaRepository.findUserByCpf(cpf).map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findAllCustomers() {
        return userJpaRepository.findAllCustomers().stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findCustomerById(Integer id) {
        return userJpaRepository.findCustomerById(id).map(userMapper::toDomain);
    }

    @Override
    public List<User> findAllCustomersThatHaveEmail() {
        return userJpaRepository.findAllCustomersThatHaveEmail().stream()
                .map(userMapper::toDomain)
                .toList();
    }

}

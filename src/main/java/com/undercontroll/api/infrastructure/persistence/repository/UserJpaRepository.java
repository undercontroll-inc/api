package com.undercontroll.api.infrastructure.persistence.repository;

import com.undercontroll.api.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByPhone(String phone);
    Optional<User> findUserByCpf(String cpf);

}

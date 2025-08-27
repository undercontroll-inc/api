package com.undercontroll.api.infrastructure.persistence.repository;

import com.undercontroll.api.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Integer> {


}

package com.undercontroll.api.repository;

import com.undercontroll.api.model.PasswordEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasswordEventRepository extends JpaRepository<PasswordEvent, UUID> {
}

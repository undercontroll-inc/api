package com.undercontroll.domain.usecase.user.impl;

import com.undercontroll.domain.usecase.user.DeleteUserPort;
import com.undercontroll.domain.exception.InvalidUserException;
import com.undercontroll.domain.exception.UserNotFoundException;
import com.undercontroll.domain.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUserImpl implements DeleteUserPort {

    private final UserGateway userGateway;

    @Override
    @CacheEvict(value = {"users", "customers", "user"}, allEntries = true)
    public void execute(Integer userId) {
        if (userId == null) {
            throw new InvalidUserException("User ID cannot be null");
        }

        var user = userGateway.findById(userId);

        if (user.isEmpty()) {
            throw new UserNotFoundException("Could not find the user with id: %d".formatted(userId));
        }

        userGateway.deleteById(userId);
        log.info("User deleted userId={}", userId);
    }
}

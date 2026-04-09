package com.undercontroll.domain.usecase.user.impl;

import com.undercontroll.domain.usecase.user.DeleteUserPort;
import com.undercontroll.domain.exception.InvalidUserException;
import com.undercontroll.domain.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUserImpl implements DeleteUserPort {

    private final UserGateway userGateway;

    @Override
    @CacheEvict(value = {"users", "customers", "user"}, allEntries = true)
    public Output execute(Input input) {
        if (input.userId() == null) {
            throw new InvalidUserException("User ID cannot be null");
        }

        var user = userGateway.findById(input.userId());

        if (user.isEmpty()) {
            throw new InvalidUserException("Could not found the user with id: %d".formatted(input.userId()));
        }

        userGateway.deleteById(input.userId());

        return new Output(true, "User deleted successfully");
    }
}

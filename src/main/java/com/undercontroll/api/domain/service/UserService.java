package com.undercontroll.api.domain.service;

import com.undercontroll.api.application.dto.*;
import com.undercontroll.api.application.port.UserPort;
import com.undercontroll.api.domain.exceptions.InvalidUserException;
import com.undercontroll.api.domain.model.User;
import com.undercontroll.api.infrastructure.persistence.adapter.UserPersistenceAdapter;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService implements UserPort {

    private final UserPersistenceAdapter adapter;

    public UserService(UserPersistenceAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        validateCreateUserRequest(request);

        User user = new User(
                request.name(),
                request.lastName(),
                request.password(),
                request.address(),
                request.cpf(),
                request.birthDate(),
                request.userType()
        );

        adapter.saveUser(user);

        return new CreateUserResponse(
                request.name(),
                request.lastName(),
                request.password(),
                request.address(),
                request.cpf(),
                request.birthDate(),
                request.userType()
        );
    }

    @Override
    public void updateUser (UpdateUserRequest request) {
        validateUpdateUser(request);

        User user = adapter.getUsersById(request.id());

        if (request.name() != null) {
            user.setName(request.name());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.address() != null) {
            user.setAddress(request.address());
        }
        if (request.userType() != null) {
            user.setUserType(request.userType());
        }
        if (request.cpf() != null) {
            user.setCpf(request.cpf());
        }
        if (request.birthDate() != null) {
            user.setBirthDate(request.birthDate());
        }
        if (request.password() != null) {
            user.setPassword(request.password());
        }

        adapter.updateUser(user);
    }

    @Override
    public List<UserDto> getUsers() {
        return adapter
                .getUsers()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public void deleteUser(Integer userId) {
        if (userId == null) {
            throw new InvalidUserException("User ID cannot be null");
        }

        adapter.deleteUser(userId);
    }

    @Override
    public UserDto getUserById(Integer userId) {
        if (userId == null) {
            throw new InvalidUserException("User ID cannot be null");
        }

        User user = adapter.getUsersById(userId);
        return mapToDto(user);
    }

    private void validateCreateUserRequest(CreateUserRequest request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new InvalidUserException("User name cannot be empty");
        }

        if (request.address() == null || request.address().trim().isEmpty()) {
            throw new InvalidUserException("User address cannot be empty");
        }

        if (request.cpf() == null || request.cpf().trim().isEmpty()) {
            throw new InvalidUserException("User cpf cannot be empty");
        }

        if (request.birthDate() == null) {
            throw new InvalidUserException("Order id must be valid");
        }

        if (request.lastName() == null || request.lastName().trim().isEmpty()) {
            throw new InvalidUserException("User last name cannot be empty");
        }

        if (request.password() == null || request.password().trim().isEmpty()) {
            throw new InvalidUserException("User password cannot be empty");
        }
    }

    private void validateUpdateUser(UpdateUserRequest request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new InvalidUserException("User name cannot be empty");
        }

        if (request.address() == null || request.address().trim().isEmpty()) {
            throw new InvalidUserException("User address cannot be empty");
        }

        if (request.cpf() == null || request.cpf().trim().isEmpty()) {
            throw new InvalidUserException("User cpf cannot be empty");
        }

        if (request.birthDate() == null) {
            throw new InvalidUserException("Order id must be valid");
        }

        if (request.lastName() == null || request.lastName().trim().isEmpty()) {
            throw new InvalidUserException("User last name cannot be empty");
        }

        if (request.password() == null || request.password().trim().isEmpty()) {
            throw new InvalidUserException("User password cannot be empty");
        }
    }

    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getName(),
                user.getLastName(),
                user.getPassword(),
                user.getAddress(),
                user.getCpf(),
                user.getBirthDate(),
                user.getUserType()
        );
    }
}
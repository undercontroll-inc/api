package com.undercontroll.api.domain.service;

import com.undercontroll.api.application.dto.*;
import com.undercontroll.api.application.port.UserPort;
import com.undercontroll.api.domain.exceptions.InvalidAuthException;
import com.undercontroll.api.domain.exceptions.InvalidUserException;
import com.undercontroll.api.domain.model.User;
import com.undercontroll.api.infrastructure.persistence.adapter.UserPersistenceAdapter;
import com.undercontroll.api.infrastructure.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserPort {

    private final UserPersistenceAdapter adapter;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        validateCreateUserRequest(request);

        String encryptedPassword = passwordEncoder.encode(request.password());

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .lastName(request.lastName())
                .password(encryptedPassword)
                .address(request.address())
                .cpf(request.cpf())
                .CEP(request.CEP())
                .phone(request.phone())
                .avatarUrl(request.avatarUrl())
                .userType(request.userType())
                .build();

        adapter.saveUser(user);

        return new CreateUserResponse(
                request.name(),
                request.email(),
                request.lastName(),
                request.address(),
                request.cpf(),
                request.CEP(),
                request.phone(),
                request.avatarUrl(),
                request.userType()
        );
    }

    @Override
    public AuthUserResponse authUser(AuthUserRequest request) {
        Optional<User> userFound = adapter.getUserByEmail(request.email());

        if(userFound.isEmpty()){
            throw new InvalidAuthException("Email or password is invalid");
        }

        boolean passwordMatch = passwordEncoder
                .matches(request.password(), userFound.get().getPassword());

        if(!passwordMatch){
            throw new InvalidAuthException("Email or password is invalid");
        }

        String token = tokenService.generateToken(userFound.get().getEmail());

        return new AuthUserResponse(
                token,
                mapToDto(userFound.get())
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
        if (request.password() != null) {
            user.setPassword(request.password());
        }

        if(request.CEP() != null){
            user.setCEP(request.CEP());
        }

        if(request.phone() != null) {
            user.setPhone(request.phone());
        }

        if(request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl());
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

        if(request.CEP() == null || request.CEP().isEmpty()){
            throw new InvalidUserException("CEP cannot be empty");
        }

        if(request.phone() == null || request.phone().isEmpty()){
            throw new InvalidUserException("Phone number cannot be empty");
        }

        if (request.address() == null || request.address().trim().isEmpty()) {
            throw new InvalidUserException("User address cannot be empty");
        }

        if (request.lastName() == null || request.lastName().trim().isEmpty()) {
            throw new InvalidUserException("User last name cannot be empty");
        }

        if (request.password() == null || request.password().trim().isEmpty()) {
            throw new InvalidUserException("User password cannot be empty");
        }
    }

    private void validateUpdateUser(UpdateUserRequest request) {
        // Validação pode ser adicionada no futuro se necessário
    }

    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail(),
                user.getLastName(),
                user.getAddress(),
                user.getCpf(),
                user.getCEP(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getUserType()
        );
    }
}
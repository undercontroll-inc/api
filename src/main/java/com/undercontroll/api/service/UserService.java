package com.undercontroll.api.service;

import com.undercontroll.api.dto.*;
import com.undercontroll.api.exception.GoogleAccountNotFoundException;
import com.undercontroll.api.exception.InvalidAuthException;
import com.undercontroll.api.exception.InvalidUserException;
import com.undercontroll.api.model.User;
import com.undercontroll.api.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserJpaRepository repository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final GoogleTokenVerifier googleTokenVerifier;

    public CreateUserResponse createUser(CreateUserRequest request) {
        validateCreateUserRequest(request);

        Optional<User> existingUserByEmail = repository.findUserByEmail(request.email());

        if(existingUserByEmail.isPresent()) {
            throw new InvalidUserException("Email is already in use");
        }

        Optional<User> existingUserByPhone = repository.findUserByPhone(request.phone());

        if (existingUserByPhone.isPresent()) {
            throw new InvalidUserException("Phone is already in use");
        }

        Optional<User> existingUserByCpf = repository.findUserByCpf(request.cpf());

        if(existingUserByCpf.isPresent()) {
            throw new InvalidUserException("CPF is already in use");
        }

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

        repository.save(user);

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

    public AuthUserResponse authUser(AuthUserRequest request) {
        Optional<User> userFound = repository.findUserByEmail(request.email());

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

    public void updateUser (UpdateUserRequest request) {
        validateUpdateUser(request);

        Optional<User> user = repository.findById(request.id());

        if(user.isEmpty()) {
            throw new InvalidUserException("Could not found the user for update with id: %d".formatted(request.id()));
        }

        User userFound = user.get();

        if (request.name() != null) {
            userFound.setName(request.name());
        }
        if (request.lastName() != null) {
            userFound.setLastName(request.lastName());
        }
        if (request.address() != null) {
            userFound.setAddress(request.address());
        }
        if (request.userType() != null) {
            userFound.setUserType(request.userType());
        }
        if (request.cpf() != null) {
            userFound.setCpf(request.cpf());
        }
        if (request.password() != null) {
            userFound.setPassword(request.password());
        }

        if(request.CEP() != null){
            userFound.setCEP(request.CEP());
        }

        if(request.phone() != null) {
            userFound.setPhone(request.phone());
        }

        if(request.avatarUrl() != null) {
            userFound.setAvatarUrl(request.avatarUrl());
        }

        repository.save(userFound);
    }

    public List<UserDto> getUsers() {
        return repository
                .findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public void deleteUser(Integer userId) {
        if (userId == null) {
            throw new InvalidUserException("User ID cannot be null");
        }

        Optional<User> user = repository.findById(userId);

        if(user.isEmpty()) {
            throw  new InvalidUserException("Could not found the user with id: %d".formatted(userId));
        }
    }

    public AuthUserResponse authUserByGoogle(AuthGoogleRequest request) {
        Optional<User> userFound = repository.findUserByEmail(request.email());

        if(userFound.isEmpty()){
            throw new GoogleAccountNotFoundException();
        }

        // Verifica se o token recebido realmente pertence ao Google e corresponde ao email enviado
        boolean valid = googleTokenVerifier.verify(request.token(), request.email());

        if(!valid){
            throw new InvalidAuthException("Google token is invalid");
        }

        String token = tokenService.generateToken(userFound.get().getEmail());

        return new AuthUserResponse(
                token,
                mapToDto(userFound.get())
        );
    }

    public User getUserById(Integer userId) {
        if (userId == null) {
            throw new InvalidUserException("User ID cannot be null");
        }

        Optional<User> user = repository.findById(userId);

        if(user.isEmpty()) {
            throw  new InvalidUserException("Could not found the user with id: %d".formatted(userId));
        }

        return user.get();
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
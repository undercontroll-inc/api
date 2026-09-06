package com.undercontroll.domain.usecase.user.impl;

import com.undercontroll.application.dto.auth.CreatePasswordEventRequest;
import com.undercontroll.application.dto.user.CreateUserRequest;
import com.undercontroll.application.dto.user.CreateUserResponse;
import com.undercontroll.application.mapper.UserDtoMapper;
import com.undercontroll.domain.usecase.user.CreateUserPort;
import com.undercontroll.domain.enums.PasswordEventType;
import com.undercontroll.domain.usecase.auth.CreatePasswordEventPort;
import com.undercontroll.infrastructure.service.NotificationService;
import com.undercontroll.infrastructure.events.UserCreatedEvent;
import com.undercontroll.domain.model.User;
import com.undercontroll.domain.enums.UserType;
import com.undercontroll.domain.exception.InvalidUserException;
import com.undercontroll.domain.gateway.UserGateway;
import com.undercontroll.infrastructure.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateUserImpl implements CreateUserPort {

    private final UserGateway userGateway;
    private final CreatePasswordEventPort createPasswordEventPort;
    private final PasswordEncoder passwordEncoder;
    private final MetricsService metricsService;
    private final NotificationService notificationService;
    private final UserDtoMapper userDtoMapper;

    @Override
    public CreateUserResponse execute(CreateUserRequest request) {
        try {
            validateCreateUserRequest(request);

            Optional<User> existingUserByEmail = userGateway.findByEmail(request.email());
            if (existingUserByEmail.isPresent()) {
                throw new InvalidUserException("Email is already in use");
            }

            Optional<User> existingUserByPhone = userGateway.findByPhone(request.phone());
            if (existingUserByPhone.isPresent()) {
                throw new InvalidUserException("Phone is already in use");
            }

            Optional<User> existingUserByCpf = userGateway.findByCpf(request.cpf());
            if (existingUserByCpf.isPresent()) {
                throw new InvalidUserException("CPF is already in use");
            }

            String password = passwordEncoder.encode(
                    request.userType().equals(UserType.ADMINISTRATOR)
                            ? request.password()
                            : createPasswordEventPort.execute(
                                    new CreatePasswordEventRequest(
                                            PasswordEventType.CREATE,
                                            null,
                                            request.phone(),
                                            null
                                    )
                            ).getValue()
            );

            User user = User.builder()
                    .name(request.name())
                    .email(request.email())
                    .lastName(request.lastName())
                    .password(password)
                    .address(request.address())
                    .cpf(request.cpf())
                    .CEP(request.CEP())
                    .phone(request.phone())
                    .avatarUrl(request.avatarUrl())
                    .hasWhatsApp(request.hasWhatsApp())
                    .alreadyRecurrent(request.alreadyRecurrent())
                    .inFirstLogin(request.inFirstLogin())
                    .userType(request.userType())
                    .build();

            User createdUser = userGateway.save(user);

            if (UserType.CUSTOMER.equals(createdUser.getUserType())) {
                notificationService.handleUserCreated(new UserCreatedEvent(
                        createdUser.getName(),
                        createdUser.getEmail(),
                        createdUser.getCreatedAt()
                ));
            }

            metricsService.incrementAccountCreated();

            return userDtoMapper.toCreateUserResponse(createdUser);
        } catch (InvalidUserException e) {
            metricsService.incrementAccountCreationFailed();
            throw e;
        }
    }

    private void validateCreateUserRequest(CreateUserRequest request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new InvalidUserException("User name cannot be empty");
        }
        if (request.CEP() == null || request.CEP().isEmpty()) {
            throw new InvalidUserException("CEP cannot be empty");
        }
        if (request.phone() == null || request.phone().isEmpty()) {
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
}

package com.undercontroll.application.usecase;

import com.undercontroll.application.port.MetricsPort;
import com.undercontroll.application.port.NotificationPort;
import com.undercontroll.application.usecase.auth.CreatePasswordEventPort;
import com.undercontroll.application.usecase.user.CreateUserPort;
import com.undercontroll.application.usecase.user.impl.CreateUserImpl;
import com.undercontroll.domain.enums.PasswordEventType;
import com.undercontroll.domain.enums.UserType;
import com.undercontroll.domain.events.UserCreatedEvent;
import com.undercontroll.domain.model.User;
import com.undercontroll.domain.repository.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private CreatePasswordEventPort createPasswordEventPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MetricsPort metricsPort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private CreateUserImpl useCase;

    @Test
    void execute_shouldPublishUserCreatedEvent_whenUserTypeIsCustomer() {
        CreateUserPort.Input input = new CreateUserPort.Input(
                "Maria",
                "maria@teste.com",
                "11999999999",
                "Silva",
                "any-pass",
                "Rua A",
                "12345678901",
                null,
                UserType.CUSTOMER,
                true,
                false,
                true,
                "03143000"
        );

        when(userRepositoryPort.findByEmail(input.email())).thenReturn(Optional.empty());
        when(userRepositoryPort.findByPhone(input.phone())).thenReturn(Optional.empty());
        when(userRepositoryPort.findByCpf(input.cpf())).thenReturn(Optional.empty());
        when(createPasswordEventPort.execute(any(CreatePasswordEventPort.Input.class)))
                .thenReturn(new CreatePasswordEventPort.Output("id", PasswordEventType.CREATE, "generated-pass", input.phone()));
        when(passwordEncoder.encode("generated-pass")).thenReturn("encoded-generated-pass");

        LocalDateTime createdAt = LocalDateTime.of(2026, 3, 30, 10, 30);
        User savedUser = User.builder()
                .name(input.name())
                .email(input.email())
                .userType(UserType.CUSTOMER)
                .createdAt(createdAt)
                .build();
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        useCase.execute(input);

        ArgumentCaptor<UserCreatedEvent> eventCaptor = ArgumentCaptor.forClass(UserCreatedEvent.class);
        verify(notificationPort).handleUserCreated(eventCaptor.capture());
        UserCreatedEvent event = eventCaptor.getValue();

        assertThat(event.name()).isEqualTo("Maria");
        assertThat(event.email()).isEqualTo("maria@teste.com");
        assertThat(event.createdAt()).isEqualTo(createdAt);
    }

    @Test
    void execute_shouldNotPublishUserCreatedEvent_whenUserTypeIsAdministrator() {
        CreateUserPort.Input input = new CreateUserPort.Input(
                "Admin",
                "admin@teste.com",
                "11888888888",
                "Master",
                "admin-pass",
                "Rua B",
                "10987654321",
                null,
                UserType.ADMINISTRATOR,
                false,
                false,
                false,
                "03143000"
        );

        when(userRepositoryPort.findByEmail(input.email())).thenReturn(Optional.empty());
        when(userRepositoryPort.findByPhone(input.phone())).thenReturn(Optional.empty());
        when(userRepositoryPort.findByCpf(input.cpf())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(input.password())).thenReturn("encoded-admin-pass");
        when(userRepositoryPort.save(any(User.class))).thenReturn(User.builder()
                .name(input.name())
                .email(input.email())
                .userType(UserType.ADMINISTRATOR)
                .createdAt(LocalDateTime.now())
                .build());

        useCase.execute(input);

        verify(notificationPort, never()).handleUserCreated(any(UserCreatedEvent.class));
    }
}

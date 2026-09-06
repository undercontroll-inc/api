package com.undercontroll.domain.usecase.auth.impl;

import com.undercontroll.application.dto.auth.CreatePasswordEventRequest;
import com.undercontroll.domain.usecase.auth.CreatePasswordEventPort;
import com.undercontroll.domain.model.PasswordEvent;
import com.undercontroll.domain.enums.PasswordEventStatus;
import com.undercontroll.domain.enums.PasswordEventType;
import com.undercontroll.domain.exception.InvalidPasswordResetException;
import com.undercontroll.domain.gateway.PasswordEventGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreatePasswordEventImpl implements CreatePasswordEventPort {

    private final PasswordEventGateway passwordEventGateway;

    @Override
    public PasswordEvent execute(CreatePasswordEventRequest request) {
        String value = request.userPhone();

        if (request.type().equals(PasswordEventType.RESET)) {
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            LocalDateTime lastWeek = now.minusDays(7);

            boolean alreadyChangedThePasswordInTheLastWeek = !passwordEventGateway
                    .findByCreatedAtBetweenAndType(lastWeek, now, PasswordEventType.RESET).isEmpty();

            if (alreadyChangedThePasswordInTheLastWeek) {
                throw new InvalidPasswordResetException("Password has already been reset in the interval of a week");
            }

            PasswordEvent activePassword = passwordEventGateway.findByStatusAndType(PasswordEventStatus.ACTIVE, PasswordEventType.RESET)
                    .orElse(null);

            if (activePassword != null) {
                activePassword.setStatus(PasswordEventStatus.USED);
                passwordEventGateway.save(activePassword);
            }

            value = request.value();
        }

        PasswordEvent passwordEvent = PasswordEvent.builder()
                .id(UUID.randomUUID())
                .type(request.type())
                .status(PasswordEventStatus.ACTIVE)
                .userAgent(request.agent())
                .value(value)
                .userPhone(request.userPhone())
                .build();

        return passwordEventGateway.save(passwordEvent);
    }
}

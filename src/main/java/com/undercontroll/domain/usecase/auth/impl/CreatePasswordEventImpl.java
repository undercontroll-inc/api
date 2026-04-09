package com.undercontroll.domain.usecase.auth.impl;

import com.undercontroll.domain.usecase.auth.CreatePasswordEventPort;
import com.undercontroll.domain.model.PasswordEvent;
import com.undercontroll.domain.enums.PasswordEventStatus;
import com.undercontroll.domain.enums.PasswordEventType;
import com.undercontroll.domain.exception.InvalidPasswordResetException;
import com.undercontroll.domain.gateway.PasswordEventGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreatePasswordEventImpl implements CreatePasswordEventPort {

    private final PasswordEventGateway passwordEventGateway;

    @Override
    public Output execute(Input input) {
        String value = input.userPhone();

        if (input.type().equals(PasswordEventType.RESET)) {
            LocalDateTime now = LocalDateTime.now();
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

            value = input.value();
        }

        PasswordEvent passwordEvent = PasswordEvent.builder()
                .id(UUID.randomUUID())
                .type(input.type())
                .status(PasswordEventStatus.ACTIVE)
                .userAgent(input.agent())
                .value(value)
                .userPhone(input.userPhone())
                .build();

        PasswordEvent saved = passwordEventGateway.save(passwordEvent);

        return new Output(
                saved.getId().toString(),
                saved.getType(),
                saved.getValue(),
                saved.getUserPhone()
        );
    }
}

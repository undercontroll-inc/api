package com.undercontroll.api.service;

import com.undercontroll.api.dto.CreatePasswordEventRequest;
import com.undercontroll.api.model.PasswordEvent;
import com.undercontroll.api.model.enums.PasswordEventStatus;
import com.undercontroll.api.model.enums.PasswordEventType;
import com.undercontroll.api.repository.PasswordEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@AllArgsConstructor
@Service
public class PasswordEventService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();
    private final PasswordEventRepository repository;

    public PasswordEvent create(CreatePasswordEventRequest request) {

        // Caso seja reset de senha o valor vai ser o código de confirmação
        // Caso não sera utilizado o telefone dele para senha de entrada
        String value = request.type().equals(PasswordEventType.RESET)
                ? generateAlphaNumericCode()
                : request.userPhone();

        PasswordEvent passwordEvent = PasswordEvent.builder()
                .id(UUID.randomUUID())
                .type(request.type())
                .status(PasswordEventStatus.ACTIVE)
                .userAgent(request.agent())
                .value(value)
                .userPhone(request.userPhone())
                .build();

        return repository.save(passwordEvent);
    }

    private static String generateAlphaNumericCode() {
        StringBuilder code = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }

}

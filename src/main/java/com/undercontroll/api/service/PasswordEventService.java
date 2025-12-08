package com.undercontroll.api.service;

import com.undercontroll.api.dto.CreatePasswordEventRequest;
import com.undercontroll.api.model.PasswordEvent;
import com.undercontroll.api.model.enums.PasswordEventStatus;
import com.undercontroll.api.model.enums.PasswordEventType;
import com.undercontroll.api.repository.PasswordEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class PasswordEventService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();
    private final PasswordEventRepository repository;

    public PasswordEvent create(CreatePasswordEventRequest request) {

        String value = request.userPhone();

        if(request.type().equals(PasswordEventType.RESET)){
            PasswordEvent activePassword = repository.findByStatusAndType(PasswordEventStatus.ACTIVE, PasswordEventType.RESET);

            if(activePassword != null){
                activePassword.setStatus(PasswordEventStatus.USED);

                repository.save(activePassword);
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

        return repository.save(passwordEvent);
    }

    public List<PasswordEvent> findEventBetween(LocalDateTime dateStart, LocalDateTime dateEnd) {
        return this.repository.findByCreatedAtBetween(dateStart, dateEnd);
    }

}

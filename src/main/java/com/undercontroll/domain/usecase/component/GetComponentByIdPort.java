package com.undercontroll.domain.usecase.component;

import com.undercontroll.application.dto.component.ComponentDto;

import java.util.Optional;

public interface GetComponentByIdPort {
    Optional<ComponentDto> execute(Integer componentId);
}

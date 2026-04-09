package com.undercontroll.domain.usecase.component;

import com.undercontroll.application.dto.ComponentDto;

public interface GetComponentByIdPort {
    record Input(
            Integer componentId
    ) {}

    record Output(
            ComponentDto component
    ) {}

    Output execute(Input input);
}

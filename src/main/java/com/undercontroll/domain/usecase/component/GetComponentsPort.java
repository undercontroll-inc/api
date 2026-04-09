package com.undercontroll.domain.usecase.component;

import com.undercontroll.application.dto.ComponentDto;

import java.util.List;

public interface GetComponentsPort {
    record Input() {}

    record Output(
            List<ComponentDto> components
    ) {}

    Output execute();
}

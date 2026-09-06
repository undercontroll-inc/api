package com.undercontroll.domain.usecase.component;

import com.undercontroll.application.dto.component.ComponentDto;

import java.util.List;

public interface GetComponentsPort {
    List<ComponentDto> execute(String category, String name);
}

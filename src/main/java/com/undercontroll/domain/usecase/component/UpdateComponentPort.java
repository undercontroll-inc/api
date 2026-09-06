package com.undercontroll.domain.usecase.component;

import com.undercontroll.application.dto.component.ComponentDto;
import com.undercontroll.application.dto.component.UpdateComponentRequest;

public interface UpdateComponentPort {
    ComponentDto execute(Integer componentId, UpdateComponentRequest request);
}

package com.undercontroll.domain.usecase.component;

import com.undercontroll.application.dto.component.ComponentDto;
import com.undercontroll.application.dto.component.RegisterComponentRequest;

public interface RegisterComponentPort {
    ComponentDto execute(RegisterComponentRequest request);
}

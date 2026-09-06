package com.undercontroll.domain.usecase.component.impl;

import com.undercontroll.application.dto.component.ComponentDto;
import com.undercontroll.application.mapper.ComponentPartDtoMapper;
import com.undercontroll.domain.usecase.component.GetComponentByIdPort;
import com.undercontroll.domain.gateway.ComponentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetComponentByIdImpl implements GetComponentByIdPort {

    private final ComponentGateway componentGateway;
    private final ComponentPartDtoMapper componentPartDtoMapper;

    @Override
    public Optional<ComponentDto> execute(Integer componentId) {
        return componentGateway.findById(componentId)
                .map(componentPartDtoMapper::toDto);
    }
}

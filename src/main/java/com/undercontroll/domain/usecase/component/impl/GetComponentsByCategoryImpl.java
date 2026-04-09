package com.undercontroll.domain.usecase.component.impl;

import com.undercontroll.domain.usecase.component.GetComponentsByCategoryPort;
import com.undercontroll.domain.gateway.ComponentGateway;
import com.undercontroll.application.dto.ComponentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetComponentsByCategoryImpl implements GetComponentsByCategoryPort {

    private final ComponentGateway componentGateway;

    @Override
    public Output execute(Input input) {
        List<ComponentDto> components = componentGateway
                .findByCategory(input.category())
                .stream()
                .map(c -> new ComponentDto(
                        c.getId(),
                        c.getName(),
                        c.getDescription(),
                        c.getBrand(),
                        c.getPrice(),
                        c.getQuantity(),
                        c.getSupplier(),
                        c.getCategory()
                ))
                .toList();
        return new Output(components);
    }
}

package com.undercontroll.application.controller.impl;

import com.undercontroll.application.dto.component.ComponentDto;
import com.undercontroll.domain.usecase.component.*;
import com.undercontroll.application.controller.ComponentApi;
import com.undercontroll.application.dto.component.RegisterComponentRequest;
import com.undercontroll.application.dto.component.UpdateComponentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ComponentController implements ComponentApi {

    private final RegisterComponentPort registerComponentPort;
    private final GetComponentsPort getComponentsPort;
    private final GetComponentByIdPort getComponentByIdPort;
    private final UpdateComponentPort updateComponentPort;
    private final DeleteComponentPort deleteComponentPort;

    @Override
    public ResponseEntity<ComponentDto> register(RegisterComponentRequest request) {
        return ResponseEntity.status(201).body(registerComponentPort.execute(request));
    }

    @Override
    public ResponseEntity<List<ComponentDto>> findAll(String category, String name) {
        return ResponseEntity.ok(getComponentsPort.execute(category, name));
    }

    @Override
    public ResponseEntity<ComponentDto> getById(Integer componentId) {
        return getComponentByIdPort.execute(componentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ComponentDto> updateComponent(UpdateComponentRequest request, Integer componentId) {
        return ResponseEntity.ok(updateComponentPort.execute(componentId, request));
    }

    @Override
    public ResponseEntity<Void> deleteComponent(Integer componentId) {
        deleteComponentPort.execute(componentId);
        return ResponseEntity.noContent().build();
    }
}

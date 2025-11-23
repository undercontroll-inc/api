package com.undercontroll.api.controller.impl;

import com.undercontroll.api.controller.ComponentApi;
import com.undercontroll.api.dto.ComponentDto;
import com.undercontroll.api.dto.RegisterComponentRequest;
import com.undercontroll.api.dto.RegisterComponentResponse;
import com.undercontroll.api.dto.UpdateComponentRequest;
import com.undercontroll.api.service.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/api/components", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ComponentController implements ComponentApi {

    private final ComponentService service;

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterComponentResponse> register(@RequestBody RegisterComponentRequest request) {
        var response = service.register(request);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ComponentDto>> findAll() {
        var response = service.getComponents();
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{componentId}")
    public ResponseEntity<ComponentDto> getById(@PathVariable Integer componentId) {
        var response = service.getComponentById(componentId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ComponentDto>> findByCategory(@PathVariable String category) {
        var response = service.getComponentsByCategory(category);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/name/{name}")
    public ResponseEntity<List<ComponentDto>> findByName(@PathVariable String name) {
        var response = service.getComponentsByCategory(name);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @Override
    @PutMapping(value = "/{componentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComponentDto> updateComponent(@RequestBody UpdateComponentRequest request, @PathVariable Integer componentId) {
        ComponentDto component = service.updateComponent(request, componentId);
        return ResponseEntity.ok(component);
    }

    @Override
    @DeleteMapping("/{componentId}")
    public ResponseEntity<Void> deleteComponent(@PathVariable Integer componentId) {
        service.deleteComponent(componentId);
        return ResponseEntity.ok().build();
    }
}

package com.undercontroll.api.controller;

import com.undercontroll.api.dto.ComponentDto;
import com.undercontroll.api.dto.RegisterComponentRequest;
import com.undercontroll.api.dto.RegisterComponentResponse;
import com.undercontroll.api.dto.UpdateComponentRequest;
import com.undercontroll.api.service.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/components")
@RequiredArgsConstructor
public class ComponentController {

    private final ComponentService service;

    @PostMapping
    public ResponseEntity<RegisterComponentResponse> register(
            @RequestBody RegisterComponentRequest request
    )  {
        var response = service.register(request);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ComponentDto>> findAll() {
        var response = service.getComponents();

        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @GetMapping("/{componentId}")
    public ResponseEntity<ComponentDto> getById(@PathVariable Integer componentId) {
        var response = service.getComponentById(componentId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ComponentDto>> findByCategory(
            @PathVariable String category
    ) {
        var response = service.getComponentsByCategory(category);

        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<ComponentDto>> findByName(
            @PathVariable String name
    ) {
        var response = service.getComponentsByCategory(name);

        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @PutMapping("/{componentId}")
    public ResponseEntity<ComponentDto> updateComponent(
            @RequestBody UpdateComponentRequest request,
            @PathVariable Integer componentId
    ) {
        ComponentDto component = service.updateComponent(request, componentId);

        return ResponseEntity.ok(component) ;
    }

    @DeleteMapping("/{componentId}")
    public ResponseEntity<Void> deleteComponent(
            @PathVariable Integer componentId
    ) {
        service.deleteComponent(componentId);

        return ResponseEntity.ok().build();
    }


}

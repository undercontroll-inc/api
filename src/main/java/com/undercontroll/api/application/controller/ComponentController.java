package com.undercontroll.api.application.controller;

import com.undercontroll.api.application.dto.ComponentDto;
import com.undercontroll.api.application.dto.RegisterComponentRequest;
import com.undercontroll.api.application.dto.RegisterComponentResponse;
import com.undercontroll.api.application.dto.UpdateComponentRequest;
import com.undercontroll.api.application.port.ComponentPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/components")
public class ComponentController {

    private final ComponentPort componentPort;

    public ComponentController(ComponentPort componentPort) {
        this.componentPort = componentPort;
    }

    @PostMapping
    public ResponseEntity<RegisterComponentResponse> register(
            @RequestBody RegisterComponentRequest request
    )  {
        var response = componentPort.register(request);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ComponentDto>> findAll() {
        var response = componentPort.getComponents();

        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ComponentDto>> findByCategory(
            @PathVariable String category
    ) {
        var response = componentPort.getComponentsByCategory(category);

        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<ComponentDto>> findByName(
            @PathVariable String name
    ) {
        var response = componentPort.getComponentsByCategory(name);

        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Void> updateComponent(
            @RequestBody UpdateComponentRequest request
    ) {
        componentPort.updateComponent(request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{componentId}")
    public ResponseEntity<Void> deleteComponent(
            @PathVariable Integer componentId
    ) {
        componentPort.deleteComponent(componentId);

        return ResponseEntity.ok().build();
    }


}

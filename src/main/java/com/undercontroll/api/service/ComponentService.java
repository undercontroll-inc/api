package com.undercontroll.api.service;

import com.undercontroll.api.dto.*;
import com.undercontroll.api.model.ComponentPort;
import com.undercontroll.api.exception.*;
import com.undercontroll.api.model.ComponentPart;
import com.undercontroll.api.model.ComponentPersistenceAdapter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentService implements ComponentPort {

    private final ComponentPersistenceAdapter adapter;

    public ComponentService(ComponentPersistenceAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public RegisterComponentResponse register(RegisterComponentRequest request) {
        validateCreate(request);

        ComponentPart component = ComponentPart.builder()
                .name(request.name())
                .description(request.description())
                .brand(request.brand())
                .price(request.price())
                .supplier(request.supplier())
                .category(request.category())
                .quantity(request.quantity() != null ? request.quantity() : 0)
                .build();

        adapter.save(component);

        return new RegisterComponentResponse(
                request.name(),
                request.description(),
                request.brand(),
                request.price(),
                request.supplier(),
                request.category()
        );
    }

    @Override
    public void updateComponent(UpdateComponentRequest request) {
        validateUpdate(request);

        ComponentPart component = adapter.findById(request.id())
                .orElseThrow(() -> new ComponentNotFoundException("Component not found with id " + request.id()));

        if(request.name() != null && !request.name().isEmpty()) {
            component.setName(request.name());
        }

        if(request.description() != null && !request.description().isEmpty()) {
            component.setDescription(request.description());
        }

        if(request.brand() != null && !request.brand().isEmpty()) {
            component.setBrand(request.brand());
        }

        if(request.category() != null && !request.category().isEmpty()) {
            component.setCategory(request.category());
        }

        if(request.price() != null) {
            component.setPrice(request.price());
        }

        if(request.supplier() != null && !request.supplier().isEmpty()) {
            component.setSupplier(request.supplier());
        }

        adapter.update(component);
    }

    @Override
    public List<ComponentDto> getComponents() {
        return adapter
                .findAll()
                .stream()
                .map(c -> new ComponentDto(
                        c.getName(),
                        c.getDescription(),
                        c.getBrand(),
                        c.getPrice(),
                        c.getSupplier(),
                        c.getCategory()
                ))
                .toList();
    }

    @Override
    public List<ComponentDto> getComponentsByCategory(String category) {
        if(category == null || category.isEmpty()){
            throw new InvalidGetComponentsByCategoryExcepiton("Category cannot be empty");
        }

        return adapter.findByCategory(category)
                .stream()
                .map(c -> new ComponentDto(
                        c.getName(),
                        c.getDescription(),
                        c.getBrand(),
                        c.getPrice(),
                        c.getSupplier(),
                        c.getCategory()
                ))
                .toList();
    }

    @Override
    public List<ComponentDto> getComponentsByName(String name) {
        if(name == null || name.isEmpty()){
            throw new InvalidGetComponentsByCategoryExcepiton("Name cannot be empty");
        }

        return adapter.findByName(name)
                .stream()
                .map(c -> new ComponentDto(
                        c.getName(),
                        c.getDescription(),
                        c.getBrand(),
                        c.getPrice(),
                        c.getSupplier(),
                        c.getCategory()
                ))
                .toList();
    }

    @Override
    public void deleteComponent(Integer componentId) {
        validateDelete(componentId);

        ComponentPart component = adapter.findById(componentId)
                .orElseThrow(() -> new ComponentNotFoundException("Component not found with id " + componentId));


        adapter.delete(component);
    }

    private void validateCreate(RegisterComponentRequest request) {
        if(
                request.name() == null || request.name().isEmpty() || request.description() == null || request.description().isEmpty() ||
                request.brand() == null || request.brand().isEmpty() || request.price() == null || request.price() <= 0 ||
                request.supplier() == null || request.supplier().isEmpty() || request.category() == null || request.category().isEmpty()
        ) {
            throw new InvalidComponentCreationException("Invalid data for the component creation");
        }
    }

    private void validateUpdate(UpdateComponentRequest request) {
        if(request.id() == null || request.id() <= 0) {
            throw new InvalidUpdateComponentException("Component id cannot be null or invalid");
        }
    }

    private void validateDelete(Integer componentId) {
        if(componentId == null || componentId <= 0) {
            throw new InvalidDeleteComponentException("Invalid id for deletion");
        }
    }

}

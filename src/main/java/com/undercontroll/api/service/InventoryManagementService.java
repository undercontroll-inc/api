package com.undercontroll.api.service;

import com.undercontroll.api.exception.ComponentNotFoundException;
import com.undercontroll.api.exception.InsuficientComponentException;
import com.undercontroll.api.model.ComponentPart;
import com.undercontroll.api.repository.ComponentJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class InventoryManagementService {

    private final ComponentJpaRepository componentRepository;

    public void decreaseStock(Integer componentId, Integer quantity) {
        log.info("Attempting to decrease stock for component {} by {} units", componentId, quantity);

        ComponentPart component = getComponentById(componentId);
        validateStockAvailability(component, quantity);

        Long newQuantity = component.getQuantity() - quantity;
        component.setQuantity(newQuantity);
        componentRepository.save(component);

        log.info("Stock decreased successfully for component {}. New quantity: {}",
                componentId, newQuantity);
    }

    public void increaseStock(Integer componentId, Integer quantity) {
        log.info("Attempting to increase stock for component {} by {} units", componentId, quantity);

        ComponentPart component = getComponentById(componentId);

        Long newQuantity = component.getQuantity() + quantity;
        component.setQuantity(newQuantity);
        componentRepository.save(component);

        log.info("Stock increased successfully for component {}. New quantity: {}",
                componentId, newQuantity);
    }

    public void validateStockAvailability(ComponentPart component, Integer requiredQuantity) {
        if (component.getQuantity() < requiredQuantity) {
            log.error("Insufficient stock for component {}. Required: {}, Available: {}",
                    component.getId(), requiredQuantity, component.getQuantity());
            throw new InsuficientComponentException(
                    String.format("Insufficient stock for component '%s' (ID: %d). Required: %d, Available: %d",
                            component.getName(), component.getId(), requiredQuantity, component.getQuantity()));
        }
    }

    public ComponentPart getComponentById(Integer componentId) {
        return componentRepository.findById(componentId)
                .orElseThrow(() -> {
                    log.error("Component not found with ID: {}", componentId);
                    return new ComponentNotFoundException(
                            "Component not found with id " + componentId);
                });
    }
}


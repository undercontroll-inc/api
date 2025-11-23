package com.undercontroll.api.service;

import com.undercontroll.api.exception.ComponentNotFoundException;
import com.undercontroll.api.exception.InsuficientComponentException;
import com.undercontroll.api.model.ComponentPart;
import com.undercontroll.api.repository.ComponentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Gerenciamento de Inventário")
class InventoryManagementServiceTest {

    @Mock
    private ComponentJpaRepository componentRepository;

    @InjectMocks
    private InventoryManagementService inventoryManagementService;

    private ComponentPart component;

    @BeforeEach
    void setUp() {
        component = new ComponentPart();
        component.setId(1);
        component.setName("Resistor 10K");
        component.setQuantity(100L);
    }

    @Test
    @DisplayName("Deve diminuir o estoque quando houver quantidade suficiente")
    void testDecreaseStock_ShouldDecreaseSuccessfully() {
        when(componentRepository.findById(1)).thenReturn(Optional.of(component));
        when(componentRepository.save(any(ComponentPart.class))).thenReturn(component);

        inventoryManagementService.decreaseStock(1, 30);

        assertEquals(70L, component.getQuantity());
        verify(componentRepository, times(1)).findById(1);
        verify(componentRepository, times(1)).save(component);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar diminuir estoque insuficiente")
    void testDecreaseStock_ShouldThrowException_WhenInsufficientStock() {
        when(componentRepository.findById(1)).thenReturn(Optional.of(component));

        InsuficientComponentException exception = assertThrows(
                InsuficientComponentException.class,
                () -> inventoryManagementService.decreaseStock(1, 150)
        );

        assertTrue(exception.getMessage().contains("Insufficient stock"));
        verify(componentRepository, times(1)).findById(1);
        verify(componentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve aumentar o estoque corretamente")
    void testIncreaseStock_ShouldIncreaseSuccessfully() {
        when(componentRepository.findById(1)).thenReturn(Optional.of(component));
        when(componentRepository.save(any(ComponentPart.class))).thenReturn(component);

        inventoryManagementService.increaseStock(1, 50);

        assertEquals(150L, component.getQuantity());
        verify(componentRepository, times(1)).findById(1);
        verify(componentRepository, times(1)).save(component);
    }

    @Test
    @DisplayName("Deve validar disponibilidade quando houver estoque suficiente")
    void testValidateStockAvailability_ShouldPass_WhenStockSufficient() {
        assertDoesNotThrow(() ->
                inventoryManagementService.validateStockAvailability(component, 50)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque for insuficiente")
    void testValidateStockAvailability_ShouldThrowException_WhenStockInsufficient() {
        InsuficientComponentException exception = assertThrows(
                InsuficientComponentException.class,
                () -> inventoryManagementService.validateStockAvailability(component, 150)
        );

        assertTrue(exception.getMessage().contains("Insufficient stock"));
        assertTrue(exception.getMessage().contains("Resistor 10K"));
    }

    @Test
    @DisplayName("Deve retornar componente quando existir")
    void testGetComponentById_ShouldReturnComponent_WhenExists() {
        when(componentRepository.findById(1)).thenReturn(Optional.of(component));

        ComponentPart result = inventoryManagementService.getComponentById(1);

        assertNotNull(result);
        assertEquals("Resistor 10K", result.getName());
        verify(componentRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção quando componente não existir")
    void testGetComponentById_ShouldThrowException_WhenNotExists() {
        when(componentRepository.findById(999)).thenReturn(Optional.empty());

        ComponentNotFoundException exception = assertThrows(
                ComponentNotFoundException.class,
                () -> inventoryManagementService.getComponentById(999)
        );

        assertTrue(exception.getMessage().contains("Component not found"));
        verify(componentRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar diminuir estoque de componente inexistente")
    void testDecreaseStock_ShouldThrowException_WhenComponentNotFound() {
        when(componentRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ComponentNotFoundException.class,
                () -> inventoryManagementService.decreaseStock(999, 10)
        );

        verify(componentRepository, times(1)).findById(999);
        verify(componentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar aumentar estoque de componente inexistente")
    void testIncreaseStock_ShouldThrowException_WhenComponentNotFound() {
        when(componentRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ComponentNotFoundException.class,
                () -> inventoryManagementService.increaseStock(999, 10)
        );

        verify(componentRepository, times(1)).findById(999);
        verify(componentRepository, never()).save(any());
    }
}

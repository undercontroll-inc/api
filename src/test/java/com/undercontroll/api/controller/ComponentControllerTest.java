package com.undercontroll.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.api.config.SecurityConfig;
import com.undercontroll.api.controller.impl.ComponentController;
import com.undercontroll.api.dto.ComponentDto;
import com.undercontroll.api.dto.RegisterComponentRequest;
import com.undercontroll.api.dto.RegisterComponentResponse;
import com.undercontroll.api.dto.UpdateComponentRequest;
import com.undercontroll.api.service.ComponentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@WebMvcTest(ComponentController.class)
class ComponentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ComponentService componentService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private Jwt createJwtToken(String userType, String email) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("userType", userType)
                .claim("sub", email)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private Jwt createAdminJwtToken() {
        return createJwtToken("ADMINISTRATOR", "admin@example.com");
    }

    private Jwt createCustomerJwtToken() {
        return createJwtToken("CUSTOMER", "customer@example.com");
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("POST /v1/api/components - ADMINISTRATOR should create component and return 201")
    void administratorShouldCreateComponentSuccessfully() throws Exception {
        RegisterComponentRequest request = new RegisterComponentRequest(
                "Resistor", "10k Ohm resistor", "Brand A", "Electronics", 100L, 1.50, "Supplier X"
        );

        RegisterComponentResponse response = new RegisterComponentResponse(
                "Resistor", "10k Ohm resistor", "Brand A", 1.50, "Supplier X", "Electronics"
        );

        when(componentService.register(any(RegisterComponentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/components")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Resistor"))
                .andExpect(jsonPath("$.price").value(1.50))
                .andExpect(jsonPath("$.category").value("Electronics"));

        verify(componentService, times(1)).register(any(RegisterComponentRequest.class));
    }

    @Test
    @DisplayName("POST /v1/api/components - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToCreateComponent() throws Exception {
        RegisterComponentRequest request = new RegisterComponentRequest(
                "Resistor", "10k Ohm resistor", "Brand A", "Electronics", 100L, 1.50, "Supplier X"
        );

        mockMvc.perform(post("/v1/api/components")
                        .with(csrf())
                        .with(jwt().jwt(createCustomerJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(componentService, never()).register(any(RegisterComponentRequest.class));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("GET /v1/api/components - ADMINISTRATOR should get all components and return 200")
    void administratorShouldGetAllComponentsSuccessfully() throws Exception {
        ComponentDto component1 = new ComponentDto(1, "Resistor", "10k Ohm", "Brand A", 1.50, 100L, "Supplier X", "Electronics");
        ComponentDto component2 = new ComponentDto(2, "Capacitor", "100uF", "Brand B", 2.00, 50L, "Supplier Y", "Electronics");

        when(componentService.getComponents()).thenReturn(List.of(component1, component2));

        mockMvc.perform(get("/v1/api/components")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item").value("Resistor"))
                .andExpect(jsonPath("$[1].item").value("Capacitor"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(componentService, times(1)).getComponents();
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("GET /v1/api/components - Should return 204 when no components found")
    void shouldReturn204WhenNoComponentsFound() throws Exception {
        when(componentService.getComponents()).thenReturn(List.of());

        mockMvc.perform(get("/v1/api/components")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(componentService, times(1)).getComponents();
    }

    @Test
    @DisplayName("GET /v1/api/components - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToGetAllComponents() throws Exception {
        mockMvc.perform(get("/v1/api/components")
                        .with(csrf())
                        .with(jwt().jwt(createCustomerJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER"))))
                .andExpect(status().isForbidden());

        verify(componentService, never()).getComponents();
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("GET /v1/api/components/{componentId} - Should get component by id and return 200")
    void shouldGetComponentByIdSuccessfully() throws Exception {
        ComponentDto component = new ComponentDto(1, "Resistor", "10k Ohm", "Brand A", 1.50, 100L, "Supplier X", "Electronics");

        when(componentService.getComponentById(1)).thenReturn(component);

        mockMvc.perform(get("/v1/api/components/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item").value("Resistor"));

        verify(componentService, times(1)).getComponentById(1);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("GET /v1/api/components/category/{category} - Should get components by category and return 200")
    void shouldGetComponentsByCategorySuccessfully() throws Exception {
        ComponentDto component1 = new ComponentDto(1, "Resistor", "10k Ohm", "Brand A", 1.50, 100L, "Supplier X", "Electronics");
        ComponentDto component2 = new ComponentDto(2, "Capacitor", "100uF", "Brand B", 2.00, 50L, "Supplier Y", "Electronics");

        when(componentService.getComponentsByCategory("Electronics")).thenReturn(List.of(component1, component2));

        mockMvc.perform(get("/v1/api/components/category/Electronics")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Electronics"))
                .andExpect(jsonPath("$[1].category").value("Electronics"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(componentService, times(1)).getComponentsByCategory("Electronics");
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("GET /v1/api/components/category/{category} - Should return 204 when no components in category")
    void shouldReturn204WhenNoCategoryComponentsFound() throws Exception {
        when(componentService.getComponentsByCategory("NonExistent")).thenReturn(List.of());

        mockMvc.perform(get("/v1/api/components/category/NonExistent")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(componentService, times(1)).getComponentsByCategory("NonExistent");
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("PUT /v1/api/components/{componentId} - ADMINISTRATOR should update component and return 200")
    void administratorShouldUpdateComponentSuccessfully() throws Exception {
        UpdateComponentRequest request = new UpdateComponentRequest(
                "Resistor Updated", "20k Ohm resistor", "Brand A", 1.75, "Supplier X", "Electronics"
        );

        ComponentDto updatedComponent = new ComponentDto(1, "Resistor Updated", "20k Ohm resistor", "Brand A", 1.75, 100L, "Supplier X", "Electronics");

        when(componentService.updateComponent(any(UpdateComponentRequest.class), eq(1))).thenReturn(updatedComponent);

        mockMvc.perform(put("/v1/api/components/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item").value("Resistor Updated"))
                .andExpect(jsonPath("$.price").value(1.75));

        verify(componentService, times(1)).updateComponent(any(UpdateComponentRequest.class), eq(1));
    }

    @Test
    @DisplayName("PUT /v1/api/components/{componentId} - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToUpdateComponent() throws Exception {
        UpdateComponentRequest request = new UpdateComponentRequest(
                "Resistor Updated", "20k Ohm resistor", "Brand A", 1.75, "Supplier X", "Electronics"
        );

        mockMvc.perform(put("/v1/api/components/1")
                        .with(csrf())
                        .with(jwt().jwt(createCustomerJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(componentService, never()).updateComponent(any(UpdateComponentRequest.class), anyInt());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("DELETE /v1/api/components/{componentId} - ADMINISTRATOR should delete component and return 200")
    void administratorShouldDeleteComponentSuccessfully() throws Exception {
        doNothing().when(componentService).deleteComponent(1);

        mockMvc.perform(delete("/v1/api/components/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(componentService, times(1)).deleteComponent(1);
    }

    @Test
    @DisplayName("DELETE /v1/api/components/{componentId} - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToDeleteComponent() throws Exception {
        mockMvc.perform(delete("/v1/api/components/1")
                        .with(csrf())
                        .with(jwt().jwt(createCustomerJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER"))))
                .andExpect(status().isForbidden());

        verify(componentService, never()).deleteComponent(anyInt());
    }
}

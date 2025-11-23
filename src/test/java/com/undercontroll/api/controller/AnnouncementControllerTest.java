package com.undercontroll.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.api.config.SecurityConfig;
import com.undercontroll.api.dto.AnnouncementDto;
import com.undercontroll.api.dto.CreateAnnouncementRequest;
import com.undercontroll.api.dto.CreateAnnouncementResponse;
import com.undercontroll.api.dto.UpdateAnnouncementRequest;
import com.undercontroll.api.model.enums.AnnouncementType;
import com.undercontroll.api.service.AnnouncementService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
@WebMvcTest(AnnouncementController.class)
class AnnouncementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnnouncementService announcementService;

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
    @DisplayName("POST /v1/api/announcements - ADMINISTRATOR should create announcement and return 201")
    void administratorShouldCreateAnnouncementSuccessfully() throws Exception {
        CreateAnnouncementRequest request = new CreateAnnouncementRequest(
                "New Feature", "We have a new feature available!", AnnouncementType.HOLIDAY
        );

        CreateAnnouncementResponse response = new CreateAnnouncementResponse(
                1, "New Feature", "We have a new feature available!", AnnouncementType.HOLIDAY,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(announcementService.createAnnouncement(any(CreateAnnouncementRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/announcements")
                        .with(csrf())
                        .with(jwt().jwt(createAdminJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_ADMINISTRATOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Feature"))
                .andExpect(jsonPath("$.type").value("HOLIDAY"));

        verify(announcementService, times(1)).createAnnouncement(any(CreateAnnouncementRequest.class));
    }

    @Test
    @DisplayName("POST /v1/api/announcements - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToCreateAnnouncement() throws Exception {
        CreateAnnouncementRequest request = new CreateAnnouncementRequest(
                "New Feature", "We have a new feature available!", AnnouncementType.HOLIDAY
        );

        mockMvc.perform(post("/v1/api/announcements")
                        .with(csrf())
                        .with(jwt().jwt(createCustomerJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(announcementService, never()).createAnnouncement(any(CreateAnnouncementRequest.class));
    }

    @Test
    @DisplayName("GET /v1/api/announcements - ADMINISTRATOR should get announcements paginated and return 200")
    void administratorShouldGetAnnouncementsPaginatedSuccessfully() throws Exception {
        AnnouncementDto announcement1 = new AnnouncementDto(
                1, "Title 1", "Content 1", AnnouncementType.HOLIDAY, LocalDateTime.now(), LocalDateTime.now()
        );
        AnnouncementDto announcement2 = new AnnouncementDto(
                2, "Title 2", "Content 2", AnnouncementType.HOLIDAY, LocalDateTime.now(), LocalDateTime.now()
        );

        when(announcementService.getAllAnnouncementsPaginated(0, 10)).thenReturn(List.of(announcement1, announcement2));

        mockMvc.perform(get("/v1/api/announcements")
                        .with(csrf())
                        .with(jwt().jwt(createAdminJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_ADMINISTRATOR")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[1].title").value("Title 2"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(announcementService, times(1)).getAllAnnouncementsPaginated(0, 10);
    }

    @Test
    @DisplayName("GET /v1/api/announcements - Should return 204 when no announcements found")
    void shouldReturn204WhenNoAnnouncementsFound() throws Exception {
        when(announcementService.getAllAnnouncementsPaginated(0, 10)).thenReturn(List.of());

        mockMvc.perform(get("/v1/api/announcements")
                        .with(csrf())
                        .with(jwt().jwt(createAdminJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_ADMINISTRATOR")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNoContent());

        verify(announcementService, times(1)).getAllAnnouncementsPaginated(0, 10);
    }

    @Test
    @DisplayName("GET /v1/api/announcements - Should use default pagination when params not provided")
    void shouldUseDefaultPaginationWhenParamsNotProvided() throws Exception {
        when(announcementService.getAllAnnouncementsPaginated(0, 10)).thenReturn(List.of());

        mockMvc.perform(get("/v1/api/announcements")
                        .with(csrf())
                        .with(jwt().jwt(createAdminJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_ADMINISTRATOR"))))
                .andExpect(status().isNoContent());

        verify(announcementService, times(1)).getAllAnnouncementsPaginated(0, 10);
    }

    @Test
    @DisplayName("GET /v1/api/announcements - CUSTOMER should be able to get announcements and return 200")
    void customerShouldGetAnnouncementsPaginatedSuccessfully() throws Exception {
        AnnouncementDto announcement1 = new AnnouncementDto(
                1, "Title 1", "Content 1", AnnouncementType.HOLIDAY, LocalDateTime.now(), LocalDateTime.now()
        );

        when(announcementService.getAllAnnouncementsPaginated(0, 10)).thenReturn(List.of(announcement1));

        mockMvc.perform(get("/v1/api/announcements")
                        .with(csrf())
                        .with(jwt().jwt(createCustomerJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title 1"));

        verify(announcementService, times(1)).getAllAnnouncementsPaginated(0, 10);
    }

    @Test
    @DisplayName("PUT /v1/api/announcements/{announcementId} - ADMINISTRATOR should update announcement and return 200")
    void administratorShouldUpdateAnnouncementSuccessfully() throws Exception {
        UpdateAnnouncementRequest request = new UpdateAnnouncementRequest(
                "Updated Title", "Updated Content", AnnouncementType.HOLIDAY
        );

        AnnouncementDto updatedAnnouncement = new AnnouncementDto(
                1, "Updated Title", "Updated Content", AnnouncementType.HOLIDAY,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(announcementService.updateAnnouncement(any(UpdateAnnouncementRequest.class), eq(1)))
                .thenReturn(updatedAnnouncement);

        mockMvc.perform(put("/v1/api/announcements/1")
                        .with(csrf())
                        .with(jwt().jwt(createAdminJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_ADMINISTRATOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.type").value("HOLIDAY"));

        verify(announcementService, times(1)).updateAnnouncement(any(UpdateAnnouncementRequest.class), eq(1));
    }

    @Test
    @DisplayName("PUT /v1/api/announcements/{announcementId} - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToUpdateAnnouncement() throws Exception {
        UpdateAnnouncementRequest request = new UpdateAnnouncementRequest(
                "Updated Title", "Updated Content", AnnouncementType.HOLIDAY
        );

        mockMvc.perform(put("/v1/api/announcements/1")
                        .with(csrf())
                        .with(jwt().jwt(createCustomerJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(announcementService, never()).updateAnnouncement(any(UpdateAnnouncementRequest.class), anyInt());
    }

    @Test
    @DisplayName("DELETE /v1/api/announcements/{announcementId} - ADMINISTRATOR should delete announcement and return 200")
    void administratorShouldDeleteAnnouncementSuccessfully() throws Exception {
        doNothing().when(announcementService).deleteAnnouncement(1);

        mockMvc.perform(delete("/v1/api/announcements/1")
                        .with(csrf())
                        .with(jwt().jwt(createAdminJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_ADMINISTRATOR"))))
                .andExpect(status().isOk());

        verify(announcementService, times(1)).deleteAnnouncement(1);
    }

    @Test
    @DisplayName("DELETE /v1/api/announcements/{announcementId} - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToDeleteAnnouncement() throws Exception {
        mockMvc.perform(delete("/v1/api/announcements/1")
                        .with(csrf())
                        .with(jwt().jwt(createCustomerJwtToken()).authorities(new SimpleGrantedAuthority("SCOPE_CUSTOMER"))))
                .andExpect(status().isForbidden());

        verify(announcementService, never()).deleteAnnouncement(anyInt());
    }
}


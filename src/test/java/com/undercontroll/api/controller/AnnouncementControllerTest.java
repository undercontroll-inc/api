package com.undercontroll.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.api.dto.AnnouncementDto;
import com.undercontroll.api.dto.CreateAnnouncementRequest;
import com.undercontroll.api.dto.CreateAnnouncementResponse;
import com.undercontroll.api.dto.UpdateAnnouncementRequest;
import com.undercontroll.api.model.enums.AnnouncementType;
import com.undercontroll.api.service.AnnouncementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnnouncementController.class)
class AnnouncementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnnouncementService announcementService;

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Feature"))
                .andExpect(jsonPath("$.type").value("HOLIDAY"));

        verify(announcementService, times(1)).createAnnouncement(any(CreateAnnouncementRequest.class));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_CUSTOMER")
    @DisplayName("POST /v1/api/announcements - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToCreateAnnouncement() throws Exception {
        CreateAnnouncementRequest request = new CreateAnnouncementRequest(
                "New Feature", "We have a new feature available!", AnnouncementType.HOLIDAY
        );

        mockMvc.perform(post("/v1/api/announcements")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(announcementService, never()).createAnnouncement(any(CreateAnnouncementRequest.class));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
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
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[1].title").value("Title 2"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(announcementService, times(1)).getAllAnnouncementsPaginated(0, 10);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("GET /v1/api/announcements - Should return 204 when no announcements found")
    void shouldReturn204WhenNoAnnouncementsFound() throws Exception {
        when(announcementService.getAllAnnouncementsPaginated(0, 10)).thenReturn(List.of());

        mockMvc.perform(get("/v1/api/announcements")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNoContent());

        verify(announcementService, times(1)).getAllAnnouncementsPaginated(0, 10);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("GET /v1/api/announcements - Should use default pagination when params not provided")
    void shouldUseDefaultPaginationWhenParamsNotProvided() throws Exception {
        when(announcementService.getAllAnnouncementsPaginated(0, 10)).thenReturn(List.of());

        mockMvc.perform(get("/v1/api/announcements")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(announcementService, times(1)).getAllAnnouncementsPaginated(0, 10);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_CUSTOMER")
    @DisplayName("GET /v1/api/announcements - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToGetAnnouncements() throws Exception {
        mockMvc.perform(get("/v1/api/announcements")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(announcementService, never()).getAllAnnouncementsPaginated(anyInt(), anyInt());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.type").value("HOLIDAY"));

        verify(announcementService, times(1)).updateAnnouncement(any(UpdateAnnouncementRequest.class), eq(1));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_CUSTOMER")
    @DisplayName("PUT /v1/api/announcements/{announcementId} - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToUpdateAnnouncement() throws Exception {
        UpdateAnnouncementRequest request = new UpdateAnnouncementRequest(
                "Updated Title", "Updated Content", AnnouncementType.HOLIDAY
        );

        mockMvc.perform(put("/v1/api/announcements/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(announcementService, never()).updateAnnouncement(any(UpdateAnnouncementRequest.class), anyInt());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ADMINISTRATOR")
    @DisplayName("DELETE /v1/api/announcements/{announcementId} - ADMINISTRATOR should delete announcement and return 200")
    void administratorShouldDeleteAnnouncementSuccessfully() throws Exception {
        doNothing().when(announcementService).deleteAnnouncement(1);

        mockMvc.perform(delete("/v1/api/announcements/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(announcementService, times(1)).deleteAnnouncement(1);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_CUSTOMER")
    @DisplayName("DELETE /v1/api/announcements/{announcementId} - CUSTOMER should be forbidden and return 403")
    void customerShouldBeForbiddenToDeleteAnnouncement() throws Exception {
        mockMvc.perform(delete("/v1/api/announcements/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(announcementService, never()).deleteAnnouncement(anyInt());
    }
}


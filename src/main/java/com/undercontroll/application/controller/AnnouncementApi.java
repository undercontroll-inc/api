package com.undercontroll.application.controller;

import com.undercontroll.infrastructure.config.ApiResponseDocumentation.*;
import com.undercontroll.application.dto.*;
import com.undercontroll.application.dto.CreateAnnouncementRequest;
import com.undercontroll.application.dto.CreateAnnouncementResponse;
import com.undercontroll.application.dto.GetPaginatedAnnouncementResponse;
import com.undercontroll.application.dto.UpdateAnnouncementRequest;
import com.undercontroll.domain.enums.AnnouncementType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Announcements", description = "APIs para gerenciamento de anúncios e comunicados")
@SecurityRequirement(name = "Bearer Authentication")
public interface AnnouncementApi {

    @Operation(summary = "Criar novo anúncio")
    @PostApiResponses
    ResponseEntity<CreateAnnouncementResponse> createAnnouncement(
            CreateAnnouncementRequest request,
            @RequestHeader("Authorization") String authHeader
    );

    @Operation(summary = "Listar anúncios paginados")
    @GetApiResponses
    ResponseEntity<GetPaginatedAnnouncementResponse> getAllAnnouncements(
            @Parameter(example = "0") Integer page,
            @Parameter(example = "10") Integer size,
            @Parameter(description = "Filtrar por tipo de anúncio") AnnouncementType type
    );

    @Operation(summary = "Atualizar anúncio")
    @PutApiResponses
    ResponseEntity<AnnouncementDto> updateAnnouncement(UpdateAnnouncementRequest request, @Parameter(example = "1") Integer announcementId);

    @Operation(summary = "Deletar anúncio")
    @DeleteApiResponses
    ResponseEntity<Void> deleteAnnouncement(@Parameter(example = "1") Integer announcementId);

    @Operation(summary = "Buscar ultimo anúncio")
    @GetApiResponses
    ResponseEntity<AnnouncementDto> getLastAnnouncement();
}

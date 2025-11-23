package com.undercontroll.api.controller;

import com.undercontroll.api.config.swagger.ApiResponseDocumentation.*;
import com.undercontroll.api.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Announcements", description = "APIs para gerenciamento de anúncios e comunicados")
@SecurityRequirement(name = "Bearer Authentication")
public interface AnnouncementApi {

    @Operation(summary = "Criar novo anúncio")
    @PostApiResponses
    ResponseEntity<CreateAnnouncementResponse> createAnnouncement(CreateAnnouncementRequest request);

    @Operation(summary = "Listar anúncios paginados")
    @GetApiResponses
    ResponseEntity<List<AnnouncementDto>> getAllAnnouncements(@Parameter(example = "0") Integer page, @Parameter(example = "10") Integer size);

    @Operation(summary = "Atualizar anúncio")
    @PutApiResponses
    ResponseEntity<AnnouncementDto> updateAnnouncement(UpdateAnnouncementRequest request, @Parameter(example = "1") Integer announcementId);

    @Operation(summary = "Deletar anúncio")
    @DeleteApiResponses
    ResponseEntity<Void> deleteAnnouncement(@Parameter(example = "1") Integer announcementId);
}


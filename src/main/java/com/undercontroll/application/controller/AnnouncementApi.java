package com.undercontroll.application.controller;

import com.undercontroll.infrastructure.config.ApiResponseDocumentation.*;
import com.undercontroll.application.dto.*;
import com.undercontroll.application.dto.CreateAnnouncementRequest;
import com.undercontroll.application.dto.CreateAnnouncementResponse;
import com.undercontroll.application.dto.GetPaginatedAnnouncementResponse;
import com.undercontroll.application.dto.UpdateAnnouncementRequest;
import com.undercontroll.application.dto.UpdateAnnouncementResponse;
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

    @Operation(
            summary = "Criar novo anúncio",
            description = "Cria o anúncio e, quando imageUpload for enviado, retorna uma URL presigned em imageUpload para o frontend enviar a imagem diretamente ao S3. O backend persiste apenas a chave da imagem."
    )
    @PostApiResponses
    ResponseEntity<CreateAnnouncementResponse> createAnnouncement(
            CreateAnnouncementRequest request,
            @RequestHeader("Authorization") String authHeader
    );

    @Operation(
            summary = "Listar anúncios paginados",
            description = "Lista anúncios e retorna imageUrl assinada para leitura quando o anúncio possuir imagem cadastrada."
    )
    @GetApiResponses
    ResponseEntity<GetPaginatedAnnouncementResponse> getAllAnnouncements(
            @Parameter(example = "0") Integer page,
            @Parameter(example = "10") Integer size,
            @Parameter(description = "Filtrar por tipo de anúncio") AnnouncementType type
    );

    @Operation(
            summary = "Atualizar anúncio",
            description = "Atualiza dados do anúncio. Envie imageUpload para trocar a imagem ou removeImage=true para remover a imagem atual; não envie ambos na mesma requisição."
    )
    @PutApiResponses
    ResponseEntity<UpdateAnnouncementResponse> updateAnnouncement(UpdateAnnouncementRequest request, @Parameter(example = "1") Integer announcementId);

    @Operation(summary = "Deletar anúncio")
    @DeleteApiResponses
    ResponseEntity<Void> deleteAnnouncement(@Parameter(example = "1") Integer announcementId);

    @Operation(
            summary = "Buscar ultimo anúncio",
            description = "Retorna o último anúncio e imageUrl assinada para leitura quando houver imagem cadastrada."
    )
    @GetApiResponses
    ResponseEntity<AnnouncementDto> getLastAnnouncement();
}

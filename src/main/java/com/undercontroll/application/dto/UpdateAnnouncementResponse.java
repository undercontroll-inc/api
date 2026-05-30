package com.undercontroll.application.dto;

import com.undercontroll.domain.enums.AnnouncementType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UpdateAnnouncementResponse(
        Integer id,
        String title,
        String content,
        @Schema(description = "URL assinada para leitura da imagem atual, quando houver")
        String imageUrl,
        @Schema(description = "Presigned PUT para upload direto ao S3 quando imageUpload foi enviado na atualização")
        GenerateUploadUrlResponse imageUpload,
        AnnouncementType type,
        LocalDateTime publishedAt,
        LocalDateTime updatedAt
) {
}

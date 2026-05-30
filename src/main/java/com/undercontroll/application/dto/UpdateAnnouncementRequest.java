package com.undercontroll.application.dto;

import com.undercontroll.domain.enums.AnnouncementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

public record UpdateAnnouncementRequest(
        String title,
        String content,
        @Valid
        @Schema(description = "Opcional. Quando informado, troca a imagem e retorna uma nova URL presigned em imageUpload.")
        AnnouncementImageUploadDto imageUpload,
        @Schema(description = "Quando true, remove a imagem atual. Não deve ser enviado junto com imageUpload.", example = "false")
        Boolean removeImage,
        AnnouncementType type
) {
}

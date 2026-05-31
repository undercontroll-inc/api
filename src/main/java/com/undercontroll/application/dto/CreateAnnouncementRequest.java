package com.undercontroll.application.dto;

import com.undercontroll.domain.enums.AnnouncementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import javax.annotation.Nullable;
import jakarta.validation.Valid;

public record CreateAnnouncementRequest(

        @NotBlank
        String title,

        @NotBlank
        String description,

        @Nullable
        @Valid
        @Schema(description = "Opcional. Quando informado, o backend retorna imageUpload com a URL presigned para upload direto ao S3.")
        AnnouncementImageUploadDto imageUpload,

        @NotNull
        AnnouncementType type
) {
}

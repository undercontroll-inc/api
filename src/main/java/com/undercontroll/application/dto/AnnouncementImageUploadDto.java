package com.undercontroll.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "Metadados da imagem do anúncio usados para gerar a URL presigned de upload")
public record AnnouncementImageUploadDto(

        @Schema(example = "recado-feriado.png")
        @NotBlank
        String originalName,

        @Schema(example = "image/png")
        @NotBlank
        String contentType,

        @Schema(example = "524288")
        @Positive
        Long sizeBytes
) {
}

package com.undercontroll.application.dto.announcement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "Announcement image metadata used to generate a presigned upload URL")
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

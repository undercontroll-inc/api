package com.undercontroll.application.dto.announcement;

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
        @Schema(description = "Optional. When provided, the backend returns imageUpload with a presigned URL for a direct S3 upload.")
        AnnouncementImageUploadDto imageUpload,

        @NotNull
        AnnouncementType type
) {
}

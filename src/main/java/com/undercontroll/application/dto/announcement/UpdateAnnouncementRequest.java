package com.undercontroll.application.dto.announcement;

import com.undercontroll.domain.enums.AnnouncementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

public record UpdateAnnouncementRequest(
        String title,
        String content,
        @Valid
        @Schema(description = "Optional. When provided, replaces the image and returns a new presigned URL in imageUpload.")
        AnnouncementImageUploadDto imageUpload,
        @Schema(description = "When true, removes the current image. Must not be sent together with imageUpload.", example = "false")
        Boolean removeImage,
        AnnouncementType type
) {
}

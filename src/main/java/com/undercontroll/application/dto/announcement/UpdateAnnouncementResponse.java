package com.undercontroll.application.dto.announcement;

import com.undercontroll.domain.enums.AnnouncementType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UpdateAnnouncementResponse(
        Integer id,
        String title,
        String content,
        @Schema(description = "Signed URL for reading the current image, when one exists")
        String imageUrl,
        @Schema(description = "Presigned PUT for a direct S3 upload when imageUpload was sent on update")
        GenerateUploadUrlResponse imageUpload,
        AnnouncementType type,
        LocalDateTime publishedAt,
        LocalDateTime updatedAt
) {
}

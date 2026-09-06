package com.undercontroll.application.dto.announcement;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload used for a direct upload to S3")
public record GenerateUploadUrlResponse(
        @Schema(description = "Presigned URL for a PUT upload", example = "https://bucket.s3.amazonaws.com/announcements/1/image.png?X-Amz-Algorithm=AWS4-HMAC-SHA256")
        @JsonProperty("presigned_url")
        String presignedUrl,

        @Schema(description = "Key persisted on the announcement to locate the image", example = "announcements/1/image.png")
        @JsonProperty("file_key")
        String fileKey,

        @Schema(description = "Presigned URL expiration timestamp in milliseconds", example = "1760000000000")
        @JsonProperty("expiration_time")
        Long expirationTime
) {}

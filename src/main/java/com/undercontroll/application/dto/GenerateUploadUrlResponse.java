package com.undercontroll.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GenerateUploadUrlResponse(
        @JsonProperty("presigned_url")
        String presignedUrl,

        @JsonProperty("file_key")
        String fileKey,

        @JsonProperty("expiration_time")
        Long expirationTime
) {}


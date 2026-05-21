package com.undercontroll.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GenerateUploadUrlRequest(
        @NotBlank(message = "File name is required")
        @JsonProperty("file_name")
        String fileName,

        @NotBlank(message = "File type (MIME type) is required")
        @JsonProperty("file_type")
        String fileType,

        @JsonProperty("expiration_minutes")
        Integer expirationMinutes
) {}


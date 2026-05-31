package com.undercontroll.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para upload direto ao S3")
public record GenerateUploadUrlResponse(
        @Schema(description = "URL presigned para upload via PUT", example = "https://bucket.s3.amazonaws.com/announcements/1/image.png?X-Amz-Algorithm=AWS4-HMAC-SHA256")
        @JsonProperty("presigned_url")
        String presignedUrl,

        @Schema(description = "Chave persistida no anúncio para localizar a imagem", example = "announcements/1/image.png")
        @JsonProperty("file_key")
        String fileKey,

        @Schema(description = "Timestamp de expiração da URL presigned em milissegundos", example = "1760000000000")
        @JsonProperty("expiration_time")
        Long expirationTime
) {}

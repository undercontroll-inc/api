package com.undercontroll.application.controller;

import com.undercontroll.application.dto.GenerateUploadUrlRequest;
import com.undercontroll.application.dto.GenerateUploadUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "File Upload", description = "APIs para gerenciamento de uploads de arquivos")
@SecurityRequirement(name = "Bearer Authentication")
public interface FileUploadApi {

    @Operation(summary = "Gerar URL presigned para upload direto ao S3")
    ResponseEntity<GenerateUploadUrlResponse> generateUploadUrl(GenerateUploadUrlRequest request);
}


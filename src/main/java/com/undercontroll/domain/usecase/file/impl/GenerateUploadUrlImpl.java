package com.undercontroll.domain.usecase.file.impl;

import com.undercontroll.domain.usecase.file.GenerateUploadUrlPort;
import com.undercontroll.infrastructure.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateUploadUrlImpl implements GenerateUploadUrlPort {

    private final StorageService storageService;

    @Value("${aws.s3.upload-bucket:undercontroll-dev-upload-bucket}")
    private String uploadBucket;

    @Value("${aws.s3.upload-expiration-minutes:15}")
    private Integer defaultExpirationMinutes;

    @Override
    public Output execute(Input input) {
        log.info("Generating presigned URL for file: {}", input.fileName());

        // Gerar chave única para o arquivo
        String fileKey = generateFileKey(input.fileName());

        // Obter tempo de expiração (usar padrão se não informado)
        Integer expirationMinutes = input.expirationMinutes() != null ?
                input.expirationMinutes() : defaultExpirationMinutes;

        // Gerar URL presigned
        var presignedUrlResponse = storageService.generatePresignedUploadUrl(
                uploadBucket,
                fileKey,
                expirationMinutes
        );

        log.info("Presigned URL generated successfully for file key: {}", fileKey);

        return new Output(
                presignedUrlResponse.presignedUrl(),
                presignedUrlResponse.fileKey(),
                presignedUrlResponse.expirationTime()
        );
    }

    /**
     * Gera uma chave única para o arquivo no S3
     * Formato: uploads/YYYY-MM-DD/UUID-filename
     */
    private String generateFileKey(String fileName) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        String uuid = UUID.randomUUID().toString();
        return String.format("uploads/%s/%s-%s", timestamp, uuid, sanitizeFileName(fileName));
    }

    /**
     * Remove caracteres especiais do nome do arquivo
     */
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}


package com.undercontroll.infrastructure.service;

import com.undercontroll.application.dto.GenerateUploadUrlResponse;
import java.util.Optional;

public interface StorageService {

    void putObject(String bucket, String key, byte[] data, Optional<String> contentType);

    GenerateUploadUrlResponse generatePresignedUploadUrl(String bucket, String key, Integer expirationMinutes);

}

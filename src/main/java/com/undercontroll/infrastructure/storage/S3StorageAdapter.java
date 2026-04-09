package com.undercontroll.infrastructure.storage;

import com.undercontroll.infrastructure.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3StorageAdapter implements StorageService {

    private final S3Client s3Client;

    @Override
    public void putObject(String bucket, String key, byte[] data, Optional<String> contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .contentType(contentType.orElse(null))
                    .key(key)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
            log.info("Object {} created in bucket {}", key, bucket);

        } catch (S3Exception e) {
            log.error("Error while putting object: {}", e.getMessage());
        }
    }
}

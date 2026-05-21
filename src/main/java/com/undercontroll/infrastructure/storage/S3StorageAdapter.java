package com.undercontroll.infrastructure.storage;

import com.undercontroll.application.dto.GenerateUploadUrlResponse;
import com.undercontroll.infrastructure.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
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

    @Override
    public GenerateUploadUrlResponse generatePresignedUploadUrl(String bucket, String key, Integer expirationMinutes) {
        try {
            S3Presigner presigner = S3Presigner.builder()
                    .region(s3Client.serviceClientConfiguration().region())
                    .credentialsProvider(s3Client.serviceClientConfiguration().credentialsProvider())
                    .build();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            PutObjectPresignRequest presignedRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expirationMinutes))
                    .putObjectRequest(putObjectRequest)
                    .build();

            var presignedPutRequest = presigner.presignPutObject(presignedRequest);
            String presignedUrl = presignedPutRequest.url().toString();

            log.info("Generated presigned URL for key {} in bucket {} with {} minutes expiration",
                    key, bucket, expirationMinutes);

            presigner.close();

            return new GenerateUploadUrlResponse(
                    presignedUrl,
                    key,
                    System.currentTimeMillis() + (expirationMinutes * 60 * 1000L)
            );

        } catch (S3Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage());
            throw new RuntimeException("Failed to generate presigned upload URL: " + e.getMessage(), e);
        }
    }
}

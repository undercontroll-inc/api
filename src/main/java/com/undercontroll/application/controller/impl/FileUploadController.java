package com.undercontroll.application.controller.impl;

import com.undercontroll.application.controller.FileUploadApi;
import com.undercontroll.application.dto.GenerateUploadUrlRequest;
import com.undercontroll.application.dto.GenerateUploadUrlResponse;
import com.undercontroll.domain.usecase.file.GenerateUploadUrlPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/v1/api/files")
@RequiredArgsConstructor
public class FileUploadController implements FileUploadApi {

    private final GenerateUploadUrlPort generateUploadUrlPort;

    @Override
    @PostMapping("/upload-url")
    public ResponseEntity<GenerateUploadUrlResponse> generateUploadUrl(
            @RequestBody GenerateUploadUrlRequest request
    ) {
        log.info("Generating presigned URL for file: {}", request.fileName());

        var output = generateUploadUrlPort.execute(
                new GenerateUploadUrlPort.Input(
                        request.fileName(),
                        request.fileType(),
                        request.expirationMinutes()
                )
        );

        return ResponseEntity.ok(
                new GenerateUploadUrlResponse(
                        output.presignedUrl(),
                        output.fileKey(),
                        output.expirationTime()
                )
        );
    }
}


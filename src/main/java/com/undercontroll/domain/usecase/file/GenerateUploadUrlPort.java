package com.undercontroll.domain.usecase.file;

import com.undercontroll.application.dto.GenerateUploadUrlResponse;

public interface GenerateUploadUrlPort {

    record Input(
            String fileName,
            String fileType,
            Integer expirationMinutes
    ) {}

    Output execute(Input input);

    record Output(
            String presignedUrl,
            String fileKey,
            Long expirationTime
    ) {}
}


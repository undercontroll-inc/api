package com.undercontroll.domain.usecase.announcement;

import com.undercontroll.application.dto.AnnouncementImageUploadDto;
import com.undercontroll.application.dto.GenerateUploadUrlResponse;
import com.undercontroll.domain.enums.AnnouncementType;

import java.time.LocalDateTime;

public interface CreateAnnouncementPort {
    record Input(
            String title,
            String description,
            String token,
            AnnouncementImageUploadDto imageUpload,
            AnnouncementType type
    ) {}

    record Output(
            Integer id,
            String title,
            String content,
            GenerateUploadUrlResponse imageUpload,
            AnnouncementType type,
            LocalDateTime publishedAt,
            LocalDateTime updatedAt
    ) {}

    Output execute(Input input);
}

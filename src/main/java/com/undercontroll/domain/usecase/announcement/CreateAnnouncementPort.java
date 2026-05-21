package com.undercontroll.domain.usecase.announcement;

import com.undercontroll.domain.enums.AnnouncementType;

import java.time.LocalDateTime;

public interface CreateAnnouncementPort {
    record Input(
            String title,
            String description,
            String imageUrl,
            String token,
            AnnouncementType type
    ) {}

    record Output(
            Integer id,
            String title,
            String content,
            String imageUrl,
            AnnouncementType type,
            LocalDateTime publishedAt,
            LocalDateTime updatedAt
    ) {}

    Output execute(Input input);
}

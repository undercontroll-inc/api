package com.undercontroll.api.dto;

import com.undercontroll.api.model.enums.AnnouncementType;

import java.time.LocalDateTime;

public record CreateAnnouncementResponse(
        Integer id,
        String title,
        String content,
        AnnouncementType type,
        LocalDateTime publishedAt,
        LocalDateTime updatedAt
) {
}

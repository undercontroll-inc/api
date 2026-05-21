package com.undercontroll.application.dto;

import com.undercontroll.domain.enums.AnnouncementType;

import java.time.LocalDateTime;

public record CreateAnnouncementResponse(
        Integer id,
        String title,
        String content,
        String imageUrl,
        AnnouncementType type,
        LocalDateTime publishedAt,
        LocalDateTime updatedAt
) {
}

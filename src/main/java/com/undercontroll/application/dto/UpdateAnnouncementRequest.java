package com.undercontroll.application.dto;

import com.undercontroll.domain.enums.AnnouncementType;

public record UpdateAnnouncementRequest(
        String title,
        String content,
        String imageUrl,
        AnnouncementType type
) {
}

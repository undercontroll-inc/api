package com.undercontroll.api.dto;

import com.undercontroll.api.model.enums.AnnouncementType;

public record UpdateAnnouncementRequest(
        String title,
        String content,
        AnnouncementType type
) {
}

package com.undercontroll.application.dto;

import java.util.List;

public record GetPaginatedAnnouncementResponse(
        List<AnnouncementDto> announcements,
        long totalElements,
        int totalPages,
        Integer page,
        Integer size
) {
}

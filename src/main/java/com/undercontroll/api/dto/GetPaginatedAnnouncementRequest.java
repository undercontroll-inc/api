package com.undercontroll.api.dto;

public record GetPaginatedAnnouncementRequest(
        Integer page,
        Integer size
) {
}

package com.undercontroll.application.dto;

public record GetPaginatedAnnouncementRequest(
        Integer page,
        Integer size
) {
}

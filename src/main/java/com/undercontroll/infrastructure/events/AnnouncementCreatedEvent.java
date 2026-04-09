package com.undercontroll.infrastructure.events;

import com.undercontroll.domain.model.Announcement;

public record AnnouncementCreatedEvent (
        Announcement announcement,
        String token
) {
}
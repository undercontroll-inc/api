package com.undercontroll.domain.usecase.announcement.impl;

import com.undercontroll.domain.usecase.announcement.CreateAnnouncementPort;
import com.undercontroll.infrastructure.service.NotificationService;
import com.undercontroll.domain.model.Announcement;
import com.undercontroll.infrastructure.events.AnnouncementCreatedEvent;
import com.undercontroll.domain.gateway.AnnouncementGateway;
import com.undercontroll.infrastructure.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateAnnouncementImpl implements CreateAnnouncementPort {

    private final AnnouncementGateway announcementGateway;
    private final NotificationService notificationService;
    private final MetricsService metricsService;

    @Override
    @CacheEvict(value = {"announcements", "lastAnnouncement"}, allEntries = true)
    public Output execute(Input input) {
        Announcement announcement = Announcement.builder()
                .title(input.title())
                .content(input.description())
                .imageUrl(input.imageUrl())
                .type(input.type())
                .build();

        Announcement announcementCreated = announcementGateway.save(announcement);

        notificationService.handleAnnouncementCreated(new AnnouncementCreatedEvent(announcementCreated, input.token()));

        metricsService.incrementAnnouncementCreated();

        return new Output(
                announcementCreated.getId(),
                announcementCreated.getTitle(),
                announcementCreated.getContent(),
                announcementCreated.getImageUrl(),
                announcementCreated.getType(),
                announcementCreated.getPublishedAt(),
                announcementCreated.getUpdatedAt()
        );
    }
}

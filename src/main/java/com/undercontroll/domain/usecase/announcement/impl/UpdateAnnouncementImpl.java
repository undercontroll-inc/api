package com.undercontroll.domain.usecase.announcement.impl;

import com.undercontroll.domain.usecase.announcement.UpdateAnnouncementPort;
import com.undercontroll.domain.model.Announcement;
import com.undercontroll.domain.exception.AnnouncementNotFoundException;
import com.undercontroll.domain.gateway.AnnouncementGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAnnouncementImpl implements UpdateAnnouncementPort {

    private final AnnouncementGateway announcementGateway;

    @Override
    @CacheEvict(value = {"announcements", "lastAnnouncement"}, allEntries = true)
    public Output execute(Input input) {
        Announcement announcement = announcementGateway
                .findById(input.id())
                .orElseThrow(() -> new AnnouncementNotFoundException(
                        "Announcement with id " + input.id() + " not found"
                ));

        if (input.title() != null) {
            announcement.setTitle(input.title());
        }

        if (input.content() != null) {
            announcement.setContent(input.content());
        }

        if (input.imageUrl() != null) {
            announcement.setImageUrl(input.imageUrl());
        }

        if (input.type() != null) {
            announcement.setType(input.type());
        }

        Announcement saved = announcementGateway.save(announcement);

        return new Output(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getImageUrl(),
                saved.getType(),
                saved.getPublishedAt(),
                saved.getUpdatedAt()
        );
    }
}

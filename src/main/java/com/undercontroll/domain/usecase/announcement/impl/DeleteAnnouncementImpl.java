package com.undercontroll.domain.usecase.announcement.impl;

import com.undercontroll.domain.usecase.announcement.DeleteAnnouncementPort;
import com.undercontroll.domain.model.Announcement;
import com.undercontroll.domain.exception.AnnouncementNotFoundException;
import com.undercontroll.domain.gateway.AnnouncementGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteAnnouncementImpl implements DeleteAnnouncementPort {

    private final AnnouncementGateway announcementGateway;

    @Override
    @CacheEvict(value = {"announcements", "lastAnnouncement"}, allEntries = true)
    public void execute(Input input) {
        Announcement announcement = announcementGateway
                .findById(input.id())
                .orElseThrow(() -> new AnnouncementNotFoundException(
                        "Announcement with id " + input.id() + " not found"
                ));

        announcementGateway.deleteById(announcement.getId());
    }
}

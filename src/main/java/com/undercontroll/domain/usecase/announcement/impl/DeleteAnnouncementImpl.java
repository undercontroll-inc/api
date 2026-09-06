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
    public void execute(Integer announcementId) {
        Announcement announcement = announcementGateway
                .findById(announcementId)
                .orElseThrow(() -> new AnnouncementNotFoundException(
                        "Announcement with id " + announcementId + " not found"
                ));

        announcementGateway.deleteById(announcement.getId());
    }
}

package com.undercontroll.domain.usecase.announcement.impl;

import com.undercontroll.domain.usecase.announcement.GetLastAnnouncementPort;
import com.undercontroll.domain.model.Announcement;
import com.undercontroll.domain.gateway.AnnouncementGateway;
import com.undercontroll.application.dto.AnnouncementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetLastAnnouncementImpl implements GetLastAnnouncementPort {

    private final AnnouncementGateway announcementGateway;

    @Override
    @Cacheable(value = "lastAnnouncement")
    public Optional<AnnouncementDto> execute() {
        return announcementGateway.findLastAnnouncement()
                .map(this::mapToDto);
    }

    private AnnouncementDto mapToDto(Announcement announcement) {
        return new AnnouncementDto(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getImageUrl(),
                announcement.getType(),
                announcement.getPublishedAt(),
                announcement.getUpdatedAt()
        );
    }
}

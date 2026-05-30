package com.undercontroll.domain.usecase.announcement.impl;

import com.undercontroll.domain.model.PaginatedResult;
import com.undercontroll.domain.usecase.announcement.GetAnnouncementsPort;
import com.undercontroll.domain.model.Announcement;
import com.undercontroll.domain.gateway.AnnouncementGateway;
import com.undercontroll.application.dto.AnnouncementDto;
import com.undercontroll.infrastructure.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAnnouncementsImpl implements GetAnnouncementsPort {

    private final AnnouncementGateway announcementGateway;
    private final StorageService storageService;

    @Value("${aws.s3.upload-bucket}")
    private String bucket;

    @Value("${aws.s3.read-expiration-minutes:1440}")
    private Long readExpirationMinutes;

    @Override
    public Output execute(Input input) {
        PaginatedResult<Announcement> result = announcementGateway
                .findAllPaginated(input.page(), input.size(), input.type());

        List<AnnouncementDto> announcements = result.content().stream()
                .map(this::mapToDto)
                .toList();

        int totalPages = input.size() > 0
                ? (int) Math.ceil((double) result.totalElements() / input.size())
                : 0;

        return new Output(announcements, result.totalElements(), totalPages);
    }

    private AnnouncementDto mapToDto(Announcement announcement) {
        return new AnnouncementDto(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                resolveImageUrl(announcement),
                announcement.getType(),
                announcement.getPublishedAt(),
                announcement.getUpdatedAt()
        );
    }

    private String resolveImageUrl(Announcement announcement) {
        if (announcement.getImageKey() == null || announcement.getImageKey().isBlank()) {
            return null;
        }

        return storageService.generateReadPresignedUrl(bucket, announcement.getImageKey(), readExpirationMinutes);
    }
}

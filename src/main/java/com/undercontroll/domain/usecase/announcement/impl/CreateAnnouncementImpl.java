package com.undercontroll.domain.usecase.announcement.impl;

import com.undercontroll.domain.usecase.announcement.CreateAnnouncementPort;
import com.undercontroll.infrastructure.service.NotificationService;
import com.undercontroll.domain.model.Announcement;
import com.undercontroll.infrastructure.events.AnnouncementCreatedEvent;
import com.undercontroll.domain.gateway.AnnouncementGateway;
import com.undercontroll.infrastructure.service.MetricsService;
import com.undercontroll.infrastructure.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateAnnouncementImpl implements CreateAnnouncementPort {

    @Value("${aws.s3.upload-bucket}")
    private String bucket;

    @Value("${aws.s3.upload-expiration-minutes:15}")
    private Integer uploadExpirationMinutes;

    private final AnnouncementGateway announcementGateway;
    private final NotificationService notificationService;
    private final MetricsService metricsService;
    private final StorageService storageService;

    @Override
    @CacheEvict(value = {"announcements", "lastAnnouncement"}, allEntries = true)
    public Output execute(Input input) {

        Announcement announcement = Announcement.builder()
                .title(input.title())
                .content(input.description())
                .type(input.type())
                .build();

        Announcement announcementCreated = announcementGateway.save(announcement);

        final var presignedUpload = input.imageUpload() == null
                ? null
                : storageService.generatePresignedUploadUrl(
                        bucket,
                        generateKey(announcementCreated.getId(), input.imageUpload().originalName(), input.imageUpload().contentType()),
                        uploadExpirationMinutes
                );

        if (presignedUpload != null) {
            announcementCreated.setImageKey(presignedUpload.fileKey());
            announcementCreated = announcementGateway.save(announcementCreated);
        }

        metricsService.incrementAnnouncementCreated();

        notificationService.handleAnnouncementCreated(new AnnouncementCreatedEvent(announcementCreated, input.token()));

        return new Output(
                announcementCreated.getId(),
                announcementCreated.getTitle(),
                announcementCreated.getContent(),
                presignedUpload,
                announcementCreated.getType(),
                announcementCreated.getPublishedAt(),
                announcementCreated.getUpdatedAt()
        );
    }

    private String generateKey(Integer announcementId, String originalName, String contentType) {
        return "announcements/%d/%s.%s".formatted(
                announcementId,
                UUID.randomUUID(),
                resolveExtension(originalName, contentType)
        );
    }

    private String resolveExtension(String originalName, String contentType) {
        String extension = extensionFromName(originalName);

        if (!extension.isBlank()) {
            return extension;
        }

        if (contentType == null || contentType.isBlank()) {
            return "bin";
        }

        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> "bin";
        };
    }

    private String extensionFromName(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            return "";
        }

        int dotIndex = originalName.lastIndexOf('.');

        if (dotIndex < 0 || dotIndex == originalName.length() - 1) {
            return "";
        }

        return originalName.substring(dotIndex + 1).replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
    }
}

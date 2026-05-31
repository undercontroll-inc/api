package com.undercontroll.domain.usecase.announcement.impl;

import com.undercontroll.domain.exception.InvalidAnnouncementException;
import com.undercontroll.domain.usecase.announcement.UpdateAnnouncementPort;
import com.undercontroll.domain.model.Announcement;
import com.undercontroll.domain.exception.AnnouncementNotFoundException;
import com.undercontroll.domain.gateway.AnnouncementGateway;
import com.undercontroll.infrastructure.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateAnnouncementImpl implements UpdateAnnouncementPort {

    private final AnnouncementGateway announcementGateway;
    private final StorageService storageService;

    @Value("${aws.s3.upload-bucket}")
    private String bucket;

    @Value("${aws.s3.upload-expiration-minutes:15}")
    private Integer uploadExpirationMinutes;

    @Value("${aws.s3.read-expiration-minutes:1440}")
    private Long readExpirationMinutes;

    @Override
    @CacheEvict(value = {"announcements", "lastAnnouncement"}, allEntries = true)
    public Output execute(Input input) {
        if (input.imageUpload() != null && Boolean.TRUE.equals(input.removeImage())) {
            throw new InvalidAnnouncementException("Cannot upload and remove an image at the same time");
        }

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

        if (Boolean.TRUE.equals(input.removeImage())) {
            announcement.setImageKey(null);
        }

        if (input.type() != null) {
            announcement.setType(input.type());
        }

        var imageUpload = input.imageUpload() == null
                ? null
                : storageService.generatePresignedUploadUrl(
                        bucket,
                        generateKey(announcement.getId(), input.imageUpload().originalName(), input.imageUpload().contentType()),
                        uploadExpirationMinutes
                );

        if (imageUpload != null) {
            announcement.setImageKey(imageUpload.fileKey());
        }

        Announcement saved = announcementGateway.save(announcement);

        return new Output(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                resolveImageUrl(saved),
                imageUpload,
                saved.getType(),
                saved.getPublishedAt(),
                saved.getUpdatedAt()
        );
    }

    private String resolveImageUrl(Announcement announcement) {
        if (announcement.getImageKey() == null || announcement.getImageKey().isBlank()) {
            return null;
        }

        return storageService.generateReadPresignedUrl(bucket, announcement.getImageKey(), readExpirationMinutes);
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

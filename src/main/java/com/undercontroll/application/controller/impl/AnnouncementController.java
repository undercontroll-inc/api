package com.undercontroll.application.controller.impl;

import com.undercontroll.domain.usecase.announcement.CreateAnnouncementPort;
import com.undercontroll.domain.usecase.announcement.DeleteAnnouncementPort;
import com.undercontroll.domain.usecase.announcement.GetAnnouncementsPort;
import com.undercontroll.domain.usecase.announcement.GetLastAnnouncementPort;
import com.undercontroll.domain.usecase.announcement.UpdateAnnouncementPort;
import com.undercontroll.application.dto.announcement.AnnouncementDto;
import com.undercontroll.application.controller.AnnouncementApi;
import com.undercontroll.application.dto.announcement.CreateAnnouncementRequest;
import com.undercontroll.application.dto.announcement.CreateAnnouncementResponse;
import com.undercontroll.application.dto.announcement.GetPaginatedAnnouncementResponse;
import com.undercontroll.application.dto.announcement.UpdateAnnouncementRequest;
import com.undercontroll.application.dto.announcement.UpdateAnnouncementResponse;
import com.undercontroll.domain.enums.AnnouncementType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AnnouncementController implements AnnouncementApi {

    private final CreateAnnouncementPort createAnnouncement;
    private final GetAnnouncementsPort getAnnouncements;
    private final UpdateAnnouncementPort updateAnnouncement;
    private final DeleteAnnouncementPort deleteAnnouncement;
    private final GetLastAnnouncementPort getLastAnnouncement;

    @Override
    public ResponseEntity<CreateAnnouncementResponse> createAnnouncement(
            CreateAnnouncementRequest request,
            String auth
    ) {
        String token = auth.split("Bearer ")[1];
        return ResponseEntity.status(201).body(createAnnouncement.execute(request, token));
    }

    @Override
    public ResponseEntity<GetPaginatedAnnouncementResponse> getAllAnnouncements(
            Integer page,
            Integer size,
            AnnouncementType type
    ) {
        return ResponseEntity.ok(getAnnouncements.execute(page, size, type));
    }

    @Override
    public ResponseEntity<UpdateAnnouncementResponse> updateAnnouncement(
            UpdateAnnouncementRequest request,
            Integer announcementId
    ) {
        return ResponseEntity.ok(updateAnnouncement.execute(announcementId, request));
    }

    @Override
    public ResponseEntity<Void> deleteAnnouncement(Integer announcementId) {
        deleteAnnouncement.execute(announcementId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AnnouncementDto> getLatestAnnouncement() {
        return getLastAnnouncement.execute()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

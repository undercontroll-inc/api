package com.undercontroll.application.controller.impl;

import com.undercontroll.domain.usecase.announcement.CreateAnnouncementPort;
import com.undercontroll.domain.usecase.announcement.DeleteAnnouncementPort;
import com.undercontroll.domain.usecase.announcement.GetAnnouncementsPort;
import com.undercontroll.domain.usecase.announcement.GetLastAnnouncementPort;
import com.undercontroll.domain.usecase.announcement.UpdateAnnouncementPort;
import com.undercontroll.application.dto.AnnouncementDto;
import com.undercontroll.application.controller.AnnouncementApi;
import com.undercontroll.application.dto.CreateAnnouncementRequest;
import com.undercontroll.application.dto.CreateAnnouncementResponse;
import com.undercontroll.application.dto.GetPaginatedAnnouncementResponse;
import com.undercontroll.application.dto.UpdateAnnouncementRequest;
import com.undercontroll.domain.enums.AnnouncementType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping(value = "/v1/api/announcements")
@RestController
public class AnnouncementController implements AnnouncementApi {

    private final CreateAnnouncementPort createAnnouncement;
    private final GetAnnouncementsPort getAnnouncements;
    private final UpdateAnnouncementPort updateAnnouncement;
    private final DeleteAnnouncementPort deleteAnnouncement;
    private final GetLastAnnouncementPort getLastAnnouncement;

    @Override
    @PostMapping
    public ResponseEntity<CreateAnnouncementResponse> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request,
            @RequestHeader("Authorization") String auth
    ) {
        String token = auth.split("Bearer ")[1];

        CreateAnnouncementPort.Output output = createAnnouncement.execute(
                new CreateAnnouncementPort.Input(request.title(), request.description(), request.imageUrl(), token, request.type())
        );
        return ResponseEntity.status(201).body(
                new CreateAnnouncementResponse(
                        output.id(),
                        output.title(),
                        output.content(),
                        output.imageUrl(),
                        output.type(),
                        output.publishedAt(),
                        output.updatedAt()
                )
        );
    }

    @Override
    @GetMapping
    public ResponseEntity<GetPaginatedAnnouncementResponse> getAllAnnouncements(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) AnnouncementType type
    ) {
        GetAnnouncementsPort.Output output = getAnnouncements.execute(
                new GetAnnouncementsPort.Input(page, size, type)
        );
        return ResponseEntity.ok(
                new GetPaginatedAnnouncementResponse(
                        output.announcements(),
                        output.totalElements(),
                        output.totalPages(),
                        page,
                        size
                )
        );
    }

    @Override
    @PutMapping(value = "/{announcementId}")
    public ResponseEntity<AnnouncementDto> updateAnnouncement(
            @Valid @RequestBody UpdateAnnouncementRequest request,
            @PathVariable Integer announcementId
    ) {
        UpdateAnnouncementPort.Output output = updateAnnouncement.execute(
                new UpdateAnnouncementPort.Input(announcementId, request.title(), request.content(), request.imageUrl(), request.type())
        );
        return ResponseEntity.ok(
                new AnnouncementDto(
                        output.id(),
                        output.title(),
                        output.content(),
                        output.imageUrl(),
                        output.type(),
                        output.publishedAt(),
                        output.updatedAt()
                )
        );
    }

    @Override
    @DeleteMapping("/{announcementId}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Integer announcementId) {
        deleteAnnouncement.execute(new DeleteAnnouncementPort.Input(announcementId));
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/last")
    public ResponseEntity<AnnouncementDto> getLastAnnouncement() {
        return getLastAnnouncement.execute()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}

package com.undercontroll.api.controller;

import com.undercontroll.api.dto.AnnouncementDto;
import com.undercontroll.api.dto.CreateAnnouncementRequest;
import com.undercontroll.api.dto.CreateAnnouncementResponse;
import com.undercontroll.api.dto.UpdateAnnouncementRequest;
import com.undercontroll.api.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/v1/api/announcements")
@RestController
public class AnnouncementController {

    private final AnnouncementService service;

    @PostMapping
    public ResponseEntity<CreateAnnouncementResponse> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request
    ) {
        var response = service.createAnnouncement(request);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementDto>> getAllAnnouncements(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        var response = service.getAllAnnouncementsPaginated(page, size);

        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @PutMapping("/{announcementId}")
    public ResponseEntity<AnnouncementDto> updateAnnouncement(
            @Valid @RequestBody UpdateAnnouncementRequest request,
            @PathVariable Integer announcementId
    ) {
        AnnouncementDto response = service.updateAnnouncement(request, announcementId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{announcementId}")
    public ResponseEntity<Void> deleteAnnouncement(
            @PathVariable Integer announcementId
    ) {
        service.deleteAnnouncement(announcementId);

        return ResponseEntity.ok().build();
    }

}

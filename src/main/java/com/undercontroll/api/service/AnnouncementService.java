package com.undercontroll.api.service;

import com.undercontroll.api.dto.AnnouncementDto;
import com.undercontroll.api.dto.CreateAnnouncementRequest;
import com.undercontroll.api.dto.CreateAnnouncementResponse;
import com.undercontroll.api.dto.UpdateAnnouncementRequest;
import com.undercontroll.api.events.AnnouncementCreatedEvent;
import com.undercontroll.api.exception.AnnouncementNotFoundException;
import com.undercontroll.api.model.Announcement;
import com.undercontroll.api.repository.AnnouncementRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final ApplicationEventPublisher publisher;
    private final MetricsService metricsService;

    public CreateAnnouncementResponse createAnnouncement(
            @Valid CreateAnnouncementRequest request
    ) {
        Announcement announcement = Announcement.builder()
                .title(request.title())
                .content(request.description())
                .type(request.type())
                .build();

        Announcement announcementCreated = announcementRepository.save(announcement);

        // publica o evento que dispara email para usuarios que possuem um email cadastrado
        // para alertar sobre o novo anuncio
        publisher.publishEvent(new AnnouncementCreatedEvent(this,announcementCreated));

        metricsService.incrementAnnouncementCreated();

        log.info("New announcement event published successfully");

        return new CreateAnnouncementResponse(
                announcementCreated.getId(),
                announcementCreated.getTitle(),
                announcementCreated.getContent(),
                announcementCreated.getType(),
                announcementCreated.getPublishedAt(),
                announcementCreated.getUpdatedAt()
        );
    }

    public AnnouncementDto updateAnnouncement(
            UpdateAnnouncementRequest request,
            Integer id
    ) {
        Announcement announcement =
                announcementRepository
                        .findById(id)
                        .orElseThrow(() -> new AnnouncementNotFoundException(
                                "Announcement with id " + id + " not found"
                        ));

        if(announcement.getTitle() != null){
            announcement.setTitle(request.title());
        }

        if(announcement.getContent() != null){
            announcement.setContent(request.content());
        }

        if(announcement.getType() != null){
            announcement.setType(request.type());
        }

        return this.mapToDto(announcementRepository.save(announcement));
    }

    public void deleteAnnouncement(Integer id){
        Announcement announcement =
                announcementRepository
                        .findById(id)
                        .orElseThrow(() -> new AnnouncementNotFoundException(
                                "Announcement with id " + id + " not found"
                        ));

        announcementRepository.delete(announcement);
    }

    public List<AnnouncementDto> getAllAnnouncementsPaginated(
            Integer page,
            Integer size
    ){
        List<Announcement> announcements = announcementRepository
                .findAllPaginated(PageRequest.of(page, size));

        return announcements.stream()
                .map(this::mapToDto)
                .toList();
    }

    public AnnouncementDto mapToDto(Announcement announcement){
        return new AnnouncementDto(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getType(),
                announcement.getPublishedAt(),
                announcement.getUpdatedAt()
        );
    }

}

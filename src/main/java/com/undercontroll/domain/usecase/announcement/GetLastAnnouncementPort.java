package com.undercontroll.domain.usecase.announcement;

import com.undercontroll.application.dto.announcement.AnnouncementDto;

import java.util.Optional;

public interface GetLastAnnouncementPort {
    Optional<AnnouncementDto> execute();
}

package com.undercontroll.domain.usecase.announcement;

import com.undercontroll.application.dto.AnnouncementDto;

import java.util.Optional;

public interface GetLastAnnouncementPort {
    Optional<AnnouncementDto> execute();
}

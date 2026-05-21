package com.undercontroll.domain.usecase.announcement;

import com.undercontroll.application.dto.AnnouncementDto;
import com.undercontroll.domain.enums.AnnouncementType;

import java.util.List;

public interface GetAnnouncementsPort {
    record Input(
            Integer page,
            Integer size,
            AnnouncementType type
    ) {}

    record Output(
            List<AnnouncementDto> announcements,
            long totalElements,
            int totalPages
    ) {}

    Output execute(Input input);
}

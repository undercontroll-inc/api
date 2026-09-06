package com.undercontroll.domain.usecase.announcement;

import com.undercontroll.application.dto.announcement.GetPaginatedAnnouncementResponse;
import com.undercontroll.domain.enums.AnnouncementType;

public interface GetAnnouncementsPort {
    GetPaginatedAnnouncementResponse execute(Integer page, Integer size, AnnouncementType type);
}

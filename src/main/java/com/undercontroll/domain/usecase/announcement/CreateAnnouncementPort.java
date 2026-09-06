package com.undercontroll.domain.usecase.announcement;

import com.undercontroll.application.dto.announcement.CreateAnnouncementRequest;
import com.undercontroll.application.dto.announcement.CreateAnnouncementResponse;

public interface CreateAnnouncementPort {
    CreateAnnouncementResponse execute(CreateAnnouncementRequest request, String token);
}

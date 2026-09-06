package com.undercontroll.domain.usecase.announcement;

import com.undercontroll.application.dto.announcement.UpdateAnnouncementRequest;
import com.undercontroll.application.dto.announcement.UpdateAnnouncementResponse;

public interface UpdateAnnouncementPort {
    UpdateAnnouncementResponse execute(Integer announcementId, UpdateAnnouncementRequest request);
}

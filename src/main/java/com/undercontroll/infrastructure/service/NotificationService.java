package com.undercontroll.infrastructure.service;

import com.undercontroll.infrastructure.events.AnnouncementCreatedEvent;
import com.undercontroll.infrastructure.events.UserCreatedEvent;

public interface NotificationService {

    void handleAnnouncementCreated(AnnouncementCreatedEvent event);

    void handleUserCreated(UserCreatedEvent event);

}

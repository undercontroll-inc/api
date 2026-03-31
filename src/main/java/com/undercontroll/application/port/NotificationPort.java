package com.undercontroll.application.port;

import com.undercontroll.domain.events.AnnouncementCreatedEvent;
import com.undercontroll.domain.events.UserCreatedEvent;

public interface NotificationPort {

    void handleAnnouncementCreated(AnnouncementCreatedEvent event);

    void handleUserCreated(UserCreatedEvent event);

}

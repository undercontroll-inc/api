package com.undercontroll.api.events;

import com.undercontroll.api.model.Announcement;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AnnouncementCreatedEvent extends ApplicationEvent {

    private final Announcement announcement;

    public AnnouncementCreatedEvent(
            Object source,
            Announcement announcement
    ) {
        super(source);
        this.announcement = announcement;
    }
}

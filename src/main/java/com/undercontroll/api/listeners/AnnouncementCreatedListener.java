package com.undercontroll.api.listeners;

import com.undercontroll.api.dto.UserDto;
import com.undercontroll.api.events.AnnouncementCreatedEvent;
import com.undercontroll.api.service.EmailService;
import com.undercontroll.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AnnouncementCreatedListener {

    private final EmailService emailService;
    private final UserService userService;

    @EventListener
    public void handleNewAnnouncementEvent(AnnouncementCreatedEvent event) {

        List<UserDto> users = userService.findAllCustomersThatHaveEmail();

        // Notifica os usuários que possuem email neste momento
        users.forEach(user -> {
            emailService.sendEmail(
                    user.email(),
                    event.getAnnouncement().getTitle(),
                    event.getAnnouncement().getContent(),
                    event
            );
        });

    }

}

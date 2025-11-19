package com.undercontroll.api.service;

import org.springframework.context.ApplicationEvent;

public interface EmailService {

    void sendEmail(String to, String subject, String body, ApplicationEvent event);

}

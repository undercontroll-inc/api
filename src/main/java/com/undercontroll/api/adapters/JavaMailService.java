package com.undercontroll.api.adapters;

import com.undercontroll.api.events.AnnouncementCreatedEvent;
import com.undercontroll.api.exception.MailSendingException;
import com.undercontroll.api.service.EmailService;
import com.undercontroll.api.service.MetricsService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class JavaMailService implements EmailService {

    private final JavaMailSender mailSender;
    private final MetricsService metricsService;

    @Value("${spring.mail.username}")
    private String from;

    public JavaMailService(JavaMailSender mailSender, MetricsService metricsService) {
        this.mailSender = mailSender;
        this.metricsService = metricsService;
    }

    @Override
    public void sendEmail(
            String to,
            String subject,
            String body,
            ApplicationEvent event
    ) {
        log.info("Sending email to {}, subject {}, body {}", to, subject, body);

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);

            String html = "";

            switch (event) {
                case AnnouncementCreatedEvent e ->
                        html = this.buildAnnouncementCreatedHtml(e, "announcement_created");
                default ->
                    log.info("Default");
            }

            helper.setText(html, true);

            mailSender.send(message);

            metricsService.incrementEmailSent();

        } catch(Exception e) {
            metricsService.incrementEmailFailed();

            throw new MailSendingException(
                    "Houve um erro ao enviar o email para %s: %s".formatted(to, e.getMessage())
            );
        }

    }

    private String buildAnnouncementCreatedHtml(
            AnnouncementCreatedEvent announcement,
            String htmlName
    ){
        String template = loadTemplate(htmlName + ".html");

        return template
                .replace("{{type}}", announcement.getAnnouncement().getType().toString())
                .replace("{{title}}", announcement.getAnnouncement().getTitle())
                .replace("{{content}}", announcement.getAnnouncement().getContent())
                .replace("{{createdAt}}",this.formatDateTime(announcement.getAnnouncement().getPublishedAt()));
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

        return dateTime.format(formatter);
    }


    private String loadTemplate(String templateName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/" + templateName)){

            if (inputStream == null) {
                throw new IllegalArgumentException("Template not found: " + templateName);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error loading the template: {}", templateName, e);
            throw new RuntimeException("Error while loading the template", e);
        }
    }
}

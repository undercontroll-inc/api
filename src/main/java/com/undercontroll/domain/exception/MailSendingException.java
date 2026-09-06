package com.undercontroll.domain.exception;

public class MailSendingException extends RuntimeException {
    public static final String CODE = "MAIL_SEND_FAILED";

    public MailSendingException(String message) {
        super(message);
    }
}

package com.sep490.gshop.service;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String body);
}

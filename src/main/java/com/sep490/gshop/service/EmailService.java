package com.sep490.gshop.service;

import org.thymeleaf.context.Context;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String body);
    void sendEmailTemplate(String toEmail, String subject, String body, String template, Context context);

}

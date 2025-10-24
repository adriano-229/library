package com.adriano.library.business.logic.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // an html email sender method
    public void sendSale(String to, String subject, String name) throws Exception {
        InputStream inputStream = new ClassPathResource("templates/email/sale.html").getInputStream();
        String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        html = html.replace("[NAME]", name);
        sendHtml(to, subject, html);
    }

    // a simple email sender method
    public void sendMessage(String to, String subject, String name) throws Exception {
        String text = "Hello " + name + ",\n\nDon't miss our sale!";
        sendText(to, subject, text);
    }

    // generic HTML sender
    public void sendHtml(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
    }

    // generic plain-text sender
    public void sendText(String to, String subject, String textBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(textBody, false);
        mailSender.send(message);
    }
}
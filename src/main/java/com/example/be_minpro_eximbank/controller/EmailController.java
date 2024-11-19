package com.example.be_minpro_eximbank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
    private JavaMailSender mailSender;

    public EmailController ( JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender ;
    }

    @GetMapping("/test-email")
    public ResponseEntity<String> sendTestEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("recipient@example.com");
            message.setSubject("Test Email");
            message.setText("This is a test email.");
            mailSender.send(message);
            return ResponseEntity.ok("Email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email: " + e.getMessage());
        }
    }
}

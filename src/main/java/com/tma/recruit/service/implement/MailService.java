package com.tma.recruit.service.implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Value("${recruit.app.email}")
    private String email;

    @Autowired
    private JavaMailSender emailSender;

    public void sendForgotPasswordByMail(String userEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo("khanhnguyenit12@gmail.com");
        message.setSubject("Forgot password");
        message.setFrom(email);
        message.setText("\nPlease use this code to reset the password for your account (" + userEmail + ")"
                + "here is your code" + token
                + "Thanks,"
                + "Recruit NG");
        emailSender.send(message);
    }
}
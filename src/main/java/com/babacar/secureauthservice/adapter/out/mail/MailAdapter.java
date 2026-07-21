package com.babacar.secureauthservice.adapter.out.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailAdapter {

    private final JavaMailSender mailSender;

    public MailAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMfaCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Votre code de vérification");
        message.setText("Votre code MFA : " + code);
        mailSender.send(message);
    }
}

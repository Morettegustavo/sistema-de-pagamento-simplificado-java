package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

@Service
public class NotificationService {
    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotification(User user, String message) throws Exception {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Notificação de Transação");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }
}

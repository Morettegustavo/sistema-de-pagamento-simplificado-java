package com.picpaysimplificado.consumers;

import com.picpaysimplificado.infra.RabbitMQConfig;
import com.picpaysimplificado.dtos.NotificationDTO;
import jakarta.mail.MessagingException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {
    private final JavaMailSender mailSender;

    public EmailConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void receiveMessage(NotificationDTO notification) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.email());
        message.setSubject("Notificação - Transaferencia Simplificada");
        message.setText(notification.message());

        mailSender.send(message);
        System.out.println("E-mail enviado para: " + notification.email());
    }
}

package com.picpaysimplificado.services;

import com.picpaysimplificado.dtos.NotificationDTO;
import com.picpaysimplificado.infra.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final RabbitTemplate rabbitTemplate;

    public NotificationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(String email, String message) {
        NotificationDTO notification = new NotificationDTO(email, message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, notification);
        System.out.println("📩 Mensagem enviada para fila: " + email);
    }
}

package com.picpaysimplificado.dtos;

import java.io.Serializable;

public record NotificationDTO(String email, String message) implements Serializable {
}

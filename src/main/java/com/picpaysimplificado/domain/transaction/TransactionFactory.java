package com.picpaysimplificado.domain.transaction;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.TransactionDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionFactory {
    public Transaction createTransaction(TransactionDTO dto, User sender, User receiver) {
        return Transaction.builder()
                .amount(dto.amount())
                .sender(sender)
                .receiver(receiver)
                .timestamp(LocalDateTime.now())
                .build();
    }

}

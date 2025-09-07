package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.transaction.TransactionFactory;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.exceptions.ErrorMessages;
import com.picpaysimplificado.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final TransactionFactory transactionFactory;
    private final AuthorizationService authorizationService;

    public TransactionService(
            UserService userService,
            TransactionRepository transactionRepository,
            NotificationService notificationService,
            TransactionFactory transactionFactory,
            AuthorizationService authorizationService
    ) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
        this.transactionFactory = transactionFactory;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception {
        User sender = this.userService.findUserByIdForUpdate(transactionDTO.senderId());
        User receiver = this.userService.findUserByIdForUpdate(transactionDTO.receiverId());

        userService.validateTransaction(sender, transactionDTO.amount());

        if (!authorizationService.authorizeTransaction(sender, transactionDTO.amount())) {
            throw new Exception(ErrorMessages.UNAUTHORIZED_TRANSACTION.getMessage());
        }

        Transaction transaction = transactionFactory.createTransaction(transactionDTO, sender, receiver);

        userService.processTransaction(sender, receiver, transactionDTO.amount());

        this.transactionRepository.save(transaction);
        this.notificationService.sendNotification(sender.getEmail(), "Transação realizada com sucesso");
        this.notificationService.sendNotification(receiver.getEmail(), "Transação recebida com sucesso");

        return transaction;
    }
}

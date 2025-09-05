package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.transaction.TransactionFactory;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepository;
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

    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception {
        User sender = this.userService.findUserById(transactionDTO.senderId());
        User receiver = this.userService.findUserById(transactionDTO.receiverId());

        userService.validateTransaction(sender, transactionDTO.amount());

        if (!authorizationService.authorizeTransaction(sender, transactionDTO.amount())) {
            throw new Exception("Transação não autorizada");
        }

        Transaction transaction = transactionFactory.createTransaction(transactionDTO, sender, receiver);

        userService.processTransaction(sender, receiver, transactionDTO.amount());

        this.transactionRepository.save(transaction);
        this.notificationService.sendNotification(sender, "Transação realizada com sucesso");
        this.notificationService.sendNotification(receiver, "Transação recebida com sucesso");

        return transaction;
    }
}

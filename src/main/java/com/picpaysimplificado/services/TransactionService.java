package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.transaction.TransactionFactory;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class TransactionService {
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final TransactionFactory transactionFactory;
    private final RestTemplate restTemplate;

    public TransactionService(
            UserService userService,
            TransactionRepository transactionRepository,
            NotificationService notificationService,
            TransactionFactory transactionFactory,
            RestTemplate restTemplate
    ) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
        this.transactionFactory = transactionFactory;
        this.restTemplate = restTemplate;
    }

    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception {
        User sender = this.userService.findUserById(transactionDTO.senderId());
        User receiver = this.userService.findUserById(transactionDTO.receiverId());

        userService.validateTransaction(sender, transactionDTO.amount());

        boolean isAuthorized = this.authorizeTransaction(sender, transactionDTO.amount());
        if(!isAuthorized){
            throw new Exception("Transação não autorizada");
        }

        Transaction transaction = transactionFactory.createTransaction(transactionDTO, sender, receiver);

        sender.setBalance(sender.getBalance().subtract(transactionDTO.amount()));
        receiver.setBalance(receiver.getBalance().add(transactionDTO.amount()));

        this.transactionRepository.save(transaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        this.notificationService.sendNotification(sender, "Transação realizada com sucesso");
        this.notificationService.sendNotification(receiver, "Transação recebida com sucesso");

        return transaction;
    }

    public boolean authorizeTransaction(User sender, BigDecimal amount){
        ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);

        if(authorizationResponse.getStatusCode() == HttpStatus.OK){
            String message = (String) authorizationResponse.getBody().get("status");
            return "success".equals(message);
        }
        return false;
    }
}

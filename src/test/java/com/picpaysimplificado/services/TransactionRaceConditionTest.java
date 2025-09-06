package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.transaction.TransactionFactory;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TransactionRaceConditionTest {

    private TransactionService transactionService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() throws Exception {
        UserService userService = mock(UserService.class);
        TransactionRepository transactionRepository = mock(TransactionRepository.class);
        NotificationService notificationService = mock(NotificationService.class);
        TransactionFactory transactionFactory = new TransactionFactory();
        AuthorizationService authorizationService = mock(AuthorizationService.class);

        transactionService = new TransactionService(
                userService, transactionRepository, notificationService, transactionFactory, authorizationService
        );

        sender = User.builder()
                .id(1L)
                .firstName("João")
                .balance(BigDecimal.valueOf(500))
                .type(UserType.COMMON)
                .build();

        receiver = User.builder()
                .id(2L)
                .firstName("Maria")
                .balance(BigDecimal.valueOf(500))
                .type(UserType.COMMON)
                .build();

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        doAnswer(invocation -> {
            User s = invocation.getArgument(0);
            User r = invocation.getArgument(1);
            BigDecimal amount = invocation.getArgument(2);

            s.setBalance(s.getBalance().subtract(amount));
            r.setBalance(r.getBalance().add(amount));
            return null;
        }).when(userService).processTransaction(any(User.class), any(User.class), any(BigDecimal.class));

        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);
    }


    @Test
    void shouldDemonstrateRaceCondition() throws Exception {
        TransactionDTO dto1 = new TransactionDTO(BigDecimal.valueOf(500), 1L, 2L);
        TransactionDTO dto2 = new TransactionDTO(BigDecimal.valueOf(500), 1L, 2L);

        System.out.println("Saldo inicial do sender: " + sender.getBalance());
        System.out.println("Saldo inicial do receiver: " + receiver.getBalance());

        CompletableFuture<Transaction> f1 = CompletableFuture.supplyAsync(() -> {
            try {
                return transactionService.createTransaction(dto1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Transaction> f2 = CompletableFuture.supplyAsync(() -> {
            try {
                return transactionService.createTransaction(dto2);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture.allOf(f1, f2).join();

        System.out.println("Saldo final do sender: " + sender.getBalance());
        System.out.println("Saldo final do receiver: " + receiver.getBalance());
        assertThat(sender.getBalance().compareTo(BigDecimal.ZERO)).isGreaterThanOrEqualTo(0);
    }
}

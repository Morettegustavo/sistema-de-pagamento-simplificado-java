package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        sender = User.builder()
                .firstName("João")
                .lastName("Silva")
                .cpf("12345678901")
                .email("joao@test.com")
                .password("123123123123")
                .balance(BigDecimal.valueOf(500))
                .type(UserType.COMMON)
                .build();

        receiver = User.builder()
                .firstName("Maria")
                .lastName("Oliveira")
                .cpf("10987654321")
                .email("maria@test.com")
                .password("123123123123")
                .balance(BigDecimal.valueOf(500))
                .type(UserType.COMMON)
                .build();

        sender = userRepository.save(sender);
        receiver = userRepository.save(receiver);
    }

    @Test
    void shouldNotAllowRaceCondition() {
        TransactionDTO dto1 = new TransactionDTO(BigDecimal.valueOf(500), sender.getId(), receiver.getId());
        TransactionDTO dto2 = new TransactionDTO(BigDecimal.valueOf(500), sender.getId(), receiver.getId());

        CompletableFuture<Transaction> f1 = CompletableFuture.supplyAsync(() -> {
            try {
                return transactionService.createTransaction(dto1);
            } catch (Exception e) {
                System.out.println("Thread 1 falhou: " + e.getMessage());
                return null;
            }
        });

        CompletableFuture<Transaction> f2 = CompletableFuture.supplyAsync(() -> {
            try {
                return transactionService.createTransaction(dto2);
            } catch (Exception e) {
                System.out.println("Thread 2 falhou: " + e.getMessage());
                return null;
            }
        });

        CompletableFuture.allOf(f1, f2).join();

        User updatedSender = userRepository.findById(sender.getId()).orElseThrow();
        User updatedReceiver = userRepository.findById(receiver.getId()).orElseThrow();

        System.out.println("Saldo final sender: " + updatedSender.getBalance());
        System.out.println("Saldo final receiver: " + updatedReceiver.getBalance());

        // ✅ garante que não ficou negativo
        assertThat(updatedSender.getBalance().compareTo(BigDecimal.ZERO)).isGreaterThanOrEqualTo(0);
    }
}

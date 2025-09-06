package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.transaction.TransactionFactory;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private TransactionFactory transactionFactory;
    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private TransactionService transactionService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        sender = User.builder()
                .id(1L)
                .email("sender@test.com")
                .firstName("Sender")
                .balance(BigDecimal.valueOf(100))
                .type(UserType.COMMON)
                .build();

        receiver = User.builder()
                .id(2L)
                .email("receiver@test.com")
                .firstName("Receiver")
                .balance(BigDecimal.valueOf(50))
                .type(UserType.COMMON)
                .build();
    }

    @Test
    void shouldCreateTransactionSuccessfully() throws Exception {
        TransactionDTO dto = new TransactionDTO(BigDecimal.TEN, sender.getId(), receiver.getId());
        Transaction transaction = new Transaction();

        when(userService.findUserById(sender.getId())).thenReturn(sender);
        when(userService.findUserById(receiver.getId())).thenReturn(receiver);
        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);
        when(transactionFactory.createTransaction(dto, sender, receiver)).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(dto);

        assertNotNull(result);
        verify(userService).processTransaction(sender, receiver, BigDecimal.TEN);
        verify(transactionRepository).save(transaction);
        verify(notificationService, times(2)).sendNotification(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenNotAuthorized() throws Exception {
        TransactionDTO dto = new TransactionDTO(BigDecimal.TEN, sender.getId(), receiver.getId());


        when(userService.findUserById(sender.getId())).thenReturn(sender);
        when(userService.findUserById(receiver.getId())).thenReturn(receiver);
        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> transactionService.createTransaction(dto));
        assertEquals("Transação não autorizada", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSenderHasInsufficientBalance() throws Exception {
        sender.setBalance(BigDecimal.ONE);
        TransactionDTO dto = new TransactionDTO(BigDecimal.TEN, sender.getId(), receiver.getId());


        when(userService.findUserById(sender.getId())).thenReturn(sender);
        when(userService.findUserById(receiver.getId())).thenReturn(receiver);

        doThrow(new Exception("Saldo insuficiente"))
                .when(userService).validateTransaction(sender, dto.amount());

        Exception ex = assertThrows(Exception.class, () -> transactionService.createTransaction(dto));
        assertEquals("Saldo insuficiente", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSenderIsShopkeeper() throws Exception {
        sender.setType(UserType.MERCHANT);
        TransactionDTO dto = new TransactionDTO(BigDecimal.TEN, sender.getId(), receiver.getId());

        when(userService.findUserById(sender.getId())).thenReturn(sender);
        when(userService.findUserById(receiver.getId())).thenReturn(receiver);

        doThrow(new Exception("Usuário do tipo Lojista não está autorizado a realizar transação"))
                .when(userService).validateTransaction(sender, dto.amount());

        Exception ex = assertThrows(Exception.class, () -> transactionService.createTransaction(dto));
        assertTrue(ex.getMessage().contains("não está autorizado"));
    }
}

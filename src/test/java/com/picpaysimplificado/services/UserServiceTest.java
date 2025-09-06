package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        UserDTO userDTO = new UserDTO("João", "Silva", "12345678901",
                new BigDecimal("100.00"), "joao@email.com", "senha123", UserType.COMMON);

        User user = User.builder()
                .firstName("João")
                .lastName("Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .password("senha123")
                .balance(new BigDecimal("100.00"))
                .type(UserType.COMMON)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        User created = userService.createUser(userDTO);

        assertNotNull(created);
        assertEquals("João", created.getFirstName());
        assertEquals("Silva", created.getLastName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInsufficient() {
        User sender = User.builder()
                .firstName("Maria")
                .type(UserType.COMMON)
                .balance(new BigDecimal("50.00"))
                .build();

        Exception ex = assertThrows(Exception.class, () ->
                userService.validateTransaction(sender, new BigDecimal("100.00"))
        );

        assertEquals("Saldo insuficiente", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserIsShopkeeper() {
        User sender = User.builder()
                .firstName("Loja X")
                .type(UserType.MERCHANT)
                .balance(new BigDecimal("500.00"))
                .build();

        Exception ex = assertThrows(Exception.class, () ->
                userService.validateTransaction(sender, new BigDecimal("100.00"))
        );

        assertEquals("Usuário do tipo Lojista não está autorizado a realizar transação", ex.getMessage());
    }

    @Test
    void shouldFindUserById() throws Exception {
        User user = User.builder()
                .id(1L)
                .firstName("Carlos")
                .build();

        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));

        User found = userService.findUserById(1L);

        assertNotNull(found);
        assertEquals("Carlos", found.getFirstName());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findUserById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () ->
                userService.findUserById(99L)
        );

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void shouldProcessTransactionCorrectly() {
        User sender = User.builder()
                .id(1L)
                .firstName("Alice")
                .balance(new BigDecimal("200.00"))
                .type(UserType.COMMON)
                .build();

        User receiver = User.builder()
                .id(2L)
                .firstName("Bob")
                .balance(new BigDecimal("100.00"))
                .type(UserType.COMMON)
                .build();

        userService.processTransaction(sender, receiver, new BigDecimal("50.00"));

        assertEquals(new BigDecimal("150.00"), sender.getBalance());
        assertEquals(new BigDecimal("150.00"), receiver.getBalance());

        verify(userRepository, times(1)).save(sender);
        verify(userRepository, times(1)).save(receiver);
    }

    @Test
    void shouldReturnAllUsers() {
        User user1 = User.builder().firstName("User1").build();
        User user2 = User.builder().firstName("User2").build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }
}

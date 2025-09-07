package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.exceptions.ErrorMessages;
import com.picpaysimplificado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        if (sender.getType() != UserType.COMMON) {
            throw new Exception(ErrorMessages.UNAUTHORIZED_TRANSACTION_NON_COMMON.getMessage());
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new Exception(ErrorMessages.INSUFFICIENT_BALANCE.getMessage());
        }
    }

    public User findUserByIdForUpdate(Long id) throws Exception {
        return this.repository.findUserByIdForUpdate(id).orElseThrow(() -> new Exception(ErrorMessages.USER_NOT_FOUND.getMessage()));
    }

    public User createUser(UserDTO userDTO) {
        User newUser = User.builder()
                .firstName(userDTO.firstName())
                .lastName(userDTO.lastName())
                .cpf(userDTO.cpf())
                .email(userDTO.email())
                .password(userDTO.password())
                .balance(userDTO.balance())
                .type(userDTO.type())
                .build();

        this.saveUser(newUser);

        return newUser;
    }

    public void processTransaction(User sender, User receiver, BigDecimal amount) {
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        saveUser(sender);
        saveUser(receiver);
    }

    public List<User> getAllUsers() {
        return this.repository.findAll();
    }

    public void saveUser(User user) {
        this.repository.save(user);
    }
}

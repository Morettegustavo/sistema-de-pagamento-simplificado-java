package com.picpaysimplificado.repositories;

import com.picpaysimplificado.domain.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByCpf(String cpf);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from users u where u.id = :id")
    Optional<User> findUserByIdForUpdate(@Param("id") Long id);

}

package com.example.easybank.repository;

import com.example.easybank.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    //Optional<Account> findByNumber(String accountNumber);

    //revisa usuario activo
    Optional<Account> findByNumberAndUser_ActiveTrue(String number);

}

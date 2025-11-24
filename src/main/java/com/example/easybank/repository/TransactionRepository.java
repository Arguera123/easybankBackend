package com.example.easybank.repository;

import com.example.easybank.domain.entity.Account;
import com.example.easybank.domain.entity.Transaction;
import com.example.easybank.domain.entity.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    public Optional<Transaction> findTransactionsByOriginAccount(Account originAccount);
    Page<Transaction> findByOriginAccount_User(UserData user, Pageable pageable);
    Page<Transaction> findByDestinationAccount_User(UserData user, Pageable pageable);
    Page<Transaction> findAll(Specification<Transaction> spec, Pageable pageable);
}

package com.example.easybank.repository;

import com.example.easybank.domain.entity.Card;
import com.example.easybank.domain.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    Optional<Card> findCardByUser(UserData userData);
}

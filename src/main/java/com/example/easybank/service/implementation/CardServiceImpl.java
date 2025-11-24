package com.example.easybank.service.implementation;

import com.example.easybank.exception.UserNotFoundException;
import com.example.easybank.service.CardService;
import com.example.easybank.domain.CreditCardData;
import com.example.easybank.domain.entity.Card;
import com.example.easybank.domain.entity.UserData;
import com.example.easybank.exception.AlreadyExistsException;
import com.example.easybank.exception.ModelNotFoundException;
import com.example.easybank.repository.CardRepository;
import com.example.easybank.repository.UserRepository;
import com.example.easybank.util.generator.RandomCreditCardGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final RandomCreditCardGenerator randomCreditCardGenerator;

    @Override
    @Transactional
    public Card create() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserData user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if(!user.getCards().isEmpty()) {
            throw new AlreadyExistsException("Card already exists");
        }

        CreditCardData cardData = randomCreditCardGenerator.generate();

        Card card = Card.builder()
                .cardNumber(cardData.cardNumber())
                .cvv(cardData.cvv())
                .expiryDate(cardData.expiration().toString())
                .account(user.getAccounts().getFirst())
                .user(user)
                .build();

        return cardRepository.save(card);
    }

    @Override
    public Card findMyOwnCard() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserData user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ModelNotFoundException("User not found"));

        Card card = user.getCards().getFirst();

        return null;
    }
}

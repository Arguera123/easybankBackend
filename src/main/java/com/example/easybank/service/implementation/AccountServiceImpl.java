package com.example.easybank.service.implementation;

import com.example.easybank.domain.dto.AccountCreateDTO;
import com.example.easybank.domain.dto.response.AccountResponseDTO;
import com.example.easybank.domain.mapper.AccountMapper;
import com.example.easybank.exception.EmptyCardListException;
import com.example.easybank.service.AccountService;
import com.example.easybank.domain.entity.Account;
import com.example.easybank.domain.entity.UserData;
import com.example.easybank.exception.AlreadyExistsException;
import com.example.easybank.exception.ModelNotFoundException;
import com.example.easybank.exception.StorageException;
import com.example.easybank.repository.AccountRepository;
import com.example.easybank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void save(AccountCreateDTO accountCreate) throws Exception {
        accountRepository.findByNumberAndUser_ActiveTrue(accountCreate.getNumber())
                .ifPresent(account -> { throw new AlreadyExistsException("Account already exists"); });

        Account account = AccountMapper.toEntity(accountCreate);

        accountRepository.save(account);

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> getAccountByNumber(String accountNumber) throws Exception {
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO getMyOwnAccount() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserData user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ModelNotFoundException("User not found"));

        if (user.getCards().isEmpty()) {
            throw new EmptyCardListException("User has no registered cards.");
        }

        Account account = user.getAccounts().getFirst();

        return AccountResponseDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accountNumber(account.getNumber())
                .balance(account.getBalance())
                .build();
    }
}

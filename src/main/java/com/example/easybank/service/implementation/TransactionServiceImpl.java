package com.example.easybank.service.implementation;

import com.example.easybank.domain.dto.request.TransactionRequestDTO;
import com.example.easybank.domain.dto.response.PageResponse;
import com.example.easybank.domain.dto.response.TransactionResponseDTO;
import com.example.easybank.domain.mapper.PageMapper;
import com.example.easybank.domain.mapper.TransactionMapper;
import com.example.easybank.exception.*;
import com.example.easybank.repository.BillRepository;
import com.example.easybank.service.TransactionService;
import com.example.easybank.domain.entity.Account;
import com.example.easybank.domain.entity.Transaction;
import com.example.easybank.domain.entity.UserData;
import com.example.easybank.repository.AccountRepository;
import com.example.easybank.repository.TransactionRepository;
import com.example.easybank.repository.UserRepository;
import com.example.easybank.util.TransactionSpecifications;
import com.example.easybank.util.generator.RandomCreditCardGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RandomCreditCardGenerator randomCreditCardGenerator;
    private final BillRepository billRepository;

    @Override
    @Transactional
    public void createTransaction(TransactionRequestDTO transactionRequestDTO) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(transactionRequestDTO);
        UserData user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ModelNotFoundException("User not found"));

        Account originAccount = accountRepository.findByNumberAndUser_ActiveTrue(user.getAccounts().getFirst().getNumber())
                .orElseThrow(() -> new ModelNotFoundException("Origin account not found"));

        Account destinationAccount = accountRepository.findByNumberAndUser_ActiveTrue(transactionRequestDTO.getAccountNumber())
                .orElseThrow(() -> new ModelNotFoundException("Destination account not found"));

        if (originAccount.getNumber().equals(destinationAccount.getNumber())) {
            throw new InvalidTransferException("Cannot transfer to the same account");
        }

        if (!destinationAccount.getUser().getFirstName().toLowerCase().equals(transactionRequestDTO.getFirstName())) {
            throw new RecipientNameMismatchException("Some recipients do not match");
        }

        if (!destinationAccount.getUser().getLastName().toLowerCase().equals(transactionRequestDTO.getLastName())) {
            throw new RecipientNameMismatchException("Some recipients do not match Last name");
        }

        BigDecimal amount = transactionRequestDTO.getAmount();
        BigDecimal originBalance = originAccount.getBalance();

        if (originBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance in the source account");
        }

        originAccount.setBalance(originBalance.subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        Transaction transaction = TransactionMapper.toEntity(transactionRequestDTO);
        transaction.setOriginAccount(originAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setType("TRANSFER");

        accountRepository.save(originAccount);

        accountRepository.save(destinationAccount);

        transactionRepository.save(transaction);
    }

    @Override
    public PageResponse<TransactionResponseDTO> getAllTransactions(
        String type,
        LocalDateTime from,
        LocalDateTime to,
        Pageable pageable
    ) throws Exception{
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserData user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ModelNotFoundException("User not found"));
        String accountNumber = user.getAccounts().getFirst().getNumber();

        Specification<Transaction> spec = Specification
            .allOf(
                TransactionSpecifications.hasType(type),
                TransactionSpecifications.betweenDates(from, to),
                TransactionSpecifications.hasAccount(accountNumber)
            );

        Page<TransactionResponseDTO> transactionsPage =
                transactionRepository.findAll(spec, pageable).map(TransactionMapper::toDTO);

        return PageMapper.map(transactionsPage);
    }
}

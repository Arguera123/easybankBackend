package com.example.easybank.domain.mapper;

import com.example.easybank.domain.dto.request.TransactionRequestDTO;
import com.example.easybank.domain.dto.response.TransactionResponseDTO;
import com.example.easybank.domain.entity.Transaction;

public class TransactionMapper {
    public static Transaction toEntity(TransactionRequestDTO transactionRequestDTO) {
        return Transaction.builder()
                .description(transactionRequestDTO.getDescription())
                .amount(transactionRequestDTO.getAmount())
                .build();
    }

    public static TransactionResponseDTO toDTO(Transaction transaction) {

        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .destinationAccountNumber(transaction.getDestinationAccount().getNumber())
                .originAccountNumber(transaction.getOriginAccount().getNumber())
                .amount(transaction.getAmount())
                .date(transaction.getDateTime())
                .description(transaction.getDescription())
                .type(transaction.getType())
                .build();
    }

}


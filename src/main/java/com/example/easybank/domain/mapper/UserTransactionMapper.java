package com.example.easybank.domain.mapper;

import com.example.easybank.domain.dto.response.AdminTransactionResponseDTO;
import com.example.easybank.domain.dto.response.UserTransactionResponseDTO;
import com.example.easybank.domain.entity.Transaction;

public class UserTransactionMapper {
    public static AdminTransactionResponseDTO toDTO(Transaction transaction) {
        return getAdminTransactionResponseDTO(transaction);
    }

    static AdminTransactionResponseDTO getAdminTransactionResponseDTO(Transaction transaction) {
        return AdminTransactionResponseDTO.builder()
                .transactionId(transaction.getId())
                .amount(transaction.getAmount())
                .date(transaction.getDateTime())
                .originAccount(
                        UserTransactionResponseDTO.builder()
                                .accountOwner(transaction.getOriginAccount().getUser().getFirstName()
                                        + " " + transaction.getOriginAccount().getUser().getLastName())
                                .accountNumber(transaction.getOriginAccount().getNumber())
                                .build()
                )
                .destinationAccount(
                        UserTransactionResponseDTO.builder()
                                .accountOwner(transaction.getDestinationAccount().getUser().getFirstName()
                                        + " " + transaction.getDestinationAccount().getUser().getLastName())
                                .accountNumber(transaction.getDestinationAccount().getNumber())
                                .build()
                )
                .build();
    }
}

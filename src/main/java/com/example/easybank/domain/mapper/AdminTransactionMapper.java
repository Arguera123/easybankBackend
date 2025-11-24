package com.example.easybank.domain.mapper;

import com.example.easybank.domain.dto.response.AdminTransactionResponseDTO;
import com.example.easybank.domain.dto.response.UserTransactionResponseDTO;
import com.example.easybank.domain.entity.Transaction;

import static com.example.easybank.domain.mapper.UserTransactionMapper.getAdminTransactionResponseDTO;

public class AdminTransactionMapper {
    public static AdminTransactionResponseDTO toDTO(Transaction transaction) {
        return getAdminTransactionResponseDTO(transaction);
    }
}

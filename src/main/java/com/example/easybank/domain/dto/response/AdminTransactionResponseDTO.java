package com.example.easybank.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminTransactionResponseDTO {
    private UUID transactionId;
    private LocalDateTime date;
    private BigDecimal amount;
    private UserTransactionResponseDTO originAccount;
    private UserTransactionResponseDTO destinationAccount;
}

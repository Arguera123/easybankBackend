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
public class TransactionResponseDTO {
    private UUID id;
    private BigDecimal amount;
    private String description;
    private String originAccountNumber;
    private String destinationAccountNumber;
    private LocalDateTime date;
    private String type;
}

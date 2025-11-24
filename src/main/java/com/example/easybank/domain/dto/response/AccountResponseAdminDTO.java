package com.example.easybank.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class AccountResponseAdminDTO {
    private UUID id;
    String firstName;
    String lastName;
    String accountNumber;
    BigDecimal balance;
}

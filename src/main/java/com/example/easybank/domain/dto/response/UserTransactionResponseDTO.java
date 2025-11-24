package com.example.easybank.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTransactionResponseDTO {
    private String accountOwner;
    private String accountNumber;
}

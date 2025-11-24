package com.example.easybank.domain.dto;

import com.example.easybank.domain.entity.UserData;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreateDTO {

    private String number;

    private String type;

    private BigDecimal balance;

    private String currency;

    private LocalDateTime createdAt;

    private UserData userData;
}

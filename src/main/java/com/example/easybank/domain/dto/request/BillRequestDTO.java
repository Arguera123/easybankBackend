package com.example.easybank.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillRequestDTO {
    private UUID id;
    private String expenseName;
    private String category;
    private BigDecimal amount;
    private Date date;
}

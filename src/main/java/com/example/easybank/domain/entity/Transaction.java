package com.example.easybank.domain.entity;

import jakarta.persistence.*;
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
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private LocalDateTime dateTime;

    @Column
    private BigDecimal amount;

    @Column
    private String type;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "origin_account_id", foreignKey = @ForeignKey(name = "fk_origin_account_transaction"))
    private Account originAccount;

    @ManyToOne
    @JoinColumn(name = "destination_account_id", foreignKey = @ForeignKey(name = "fk_destination_account_transaction"))
    private Account destinationAccount;
}

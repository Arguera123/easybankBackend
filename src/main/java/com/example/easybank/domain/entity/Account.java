package com.example.easybank.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String number;

    @Column
    private String type;

    @Column
    private BigDecimal balance;

    @Column
    private String currency;

    @Column
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "originAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Transaction> originTransactions;

    @OneToMany(mappedBy = "destinationAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Transaction> destinationTransactions;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_account"))
    private UserData user;
}

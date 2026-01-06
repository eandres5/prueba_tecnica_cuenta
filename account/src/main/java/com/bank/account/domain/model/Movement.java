package com.bank.account.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Movement entity representing a transaction on an account.
 * Movements can be of type CREDIT (deposit) or DEBIT (withdrawal).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("movements")
public class Movement {

    @Id
    @Column("movement_id")
    private Long movementId;

    @Column("movement_date")
    private LocalDateTime movementDate;

    @Column("movement_type")
    private String movementType; // CREDIT, DEBIT

    @Column("amount")
    private BigDecimal amount;

    @Column("balance")
    private BigDecimal balance;

    @Column("account_id")
    private Long accountId;

    @Column("created_at")
    private LocalDateTime createdAt;
}
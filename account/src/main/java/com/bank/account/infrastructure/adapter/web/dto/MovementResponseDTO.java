package com.bank.account.infrastructure.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for movement data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementResponseDTO {

    @JsonProperty("movement_id")
    private Long movementId;
    @JsonProperty("movement_date")
    private LocalDateTime movementDate;
    @JsonProperty("movement_type")
    private String movementType;
    private BigDecimal amount;
    private BigDecimal balance;
    @JsonProperty("account_id")
    private Long accountId;
    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
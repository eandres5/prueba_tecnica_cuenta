package com.bank.account.infrastructure.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new movement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementRequestDTO {

    @NotNull(message = "Account ID is required")
    @JsonProperty("account_id")
    private Long accountId;

    @NotBlank(message = "Movement type is required")
    @Pattern(regexp = "^(CREDIT|DEBIT)$", message = "Movement type must be 'CREDIT' or 'DEBIT'")
    @JsonProperty("movement_type")
    private String movementType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
}
package com.bank.account.infrastructure.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for creating a new account
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO {

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^\\d{6,12}$", message = "Account number must be between 6 and 12 digits")
    @JsonProperty("account_number")
    private String accountNumber;
    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "^(Ahorro|Corriente)$", message = "Account type must be 'Ahorro' or 'Corriente'")
    @JsonProperty("account_type")
    private String accountType;
    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Initial balance must be greater than or equal to 0")
    @JsonProperty("initial_balance")
    private BigDecimal initialBalance;
    @NotNull(message = "Status is required")
    private Boolean status;
    @NotNull(message = "Customer ID is required")
    @JsonProperty("customer_id")
    private Long customerId;
}
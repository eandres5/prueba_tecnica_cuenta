package com.bank.account.infrastructure.adapter.web.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementDTO {

    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("account_type")
    private String accountType;
    @JsonProperty("initial_balance")
    private BigDecimal initialBalance;
    @JsonProperty("current_balance")
    private BigDecimal currentBalance;
    private Boolean status;
    @JsonProperty("movements")
    private List<MovementDetailDTO> movements;
}
package com.bank.account.infrastructure.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating account data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateDTO {

    @Pattern(regexp = "^(Ahorro|Corriente)$", message = "Account type must be 'Ahorro' or 'Corriente'")
    @JsonProperty("account_type")
    private String accountType;

    private Boolean status;
}

package com.bank.account.infrastructure.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementDetailDTO {

    @JsonProperty("movement_date")
    private LocalDateTime movementDate;

    @JsonProperty("movement_type")
    private String movementType;
    private BigDecimal amount;
    private BigDecimal balance;
}
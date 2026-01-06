package com.bank.account.infrastructure.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event DTO for account lifecycle events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEventDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String eventType;
    private Long accountId;
    private String accountNumber;
    private String accountType;
    private BigDecimal currentBalance;
    private Long customerId;
    private Boolean status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}

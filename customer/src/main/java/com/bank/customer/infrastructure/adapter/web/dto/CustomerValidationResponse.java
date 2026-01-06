package com.bank.customer.infrastructure.adapter.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerValidationResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String correlationId;
    private Long customerId;
    private boolean isValid;
    private boolean isActive;
    private String customerName;
    private String message;
}

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
public class CustomerValidationRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long customerId;
    private String correlationId;
}

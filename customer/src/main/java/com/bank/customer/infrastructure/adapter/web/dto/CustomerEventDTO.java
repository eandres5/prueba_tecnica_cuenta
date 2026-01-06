package com.bank.customer.infrastructure.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Customer Event DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEventDTO {
    private String eventType;
    private Long customerId;
    private String customerName;
    private String identification;
    private Boolean status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}
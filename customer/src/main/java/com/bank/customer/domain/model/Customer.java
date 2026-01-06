package com.bank.customer.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Customer entity extending Person with additional customer-specific attributes.
 * Represents a bank customer in the system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("customers")
public class Customer {

    @Id
    @Column("customer_id")
    private Long customerId;

    @Column("person_id")
    private Long personId;

    @Column("password")
    private String password;

    @Column("status")
    private Boolean status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
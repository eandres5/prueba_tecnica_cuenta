package com.bank.customer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Base Person entity representing common attributes for all persons in the system.
 * This entity serves as a parent class for Customer entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("persons")
public class Person {

    @Id
    @Column("person_id")
    private Long personId;

    @Column("name")
    private String name;

    @Column("gender")
    private String gender;

    @Column("identification")
    private String identification;

    @Column("address")
    private String address;

    @Column("phone")
    private String phone;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
package com.bank.customer.infrastructure.adapter.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating customer data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateDTO {
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;
    @Pattern(regexp = "^\\d{9,15}$", message = "Phone must be between 9 and 15 digits")
    private String phone;
    @Size(min = 4, max = 100, message = "Password must be between 4 and 100 characters")
    private String password;
    private Boolean status;
}

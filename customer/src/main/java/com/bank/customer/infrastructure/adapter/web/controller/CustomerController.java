package com.bank.customer.infrastructure.adapter.web.controller;

import com.bank.customer.application.service.CustomerService;
import com.bank.customer.infrastructure.adapter.web.dto.CustomerRequestDTO;
import com.bank.customer.infrastructure.adapter.web.dto.CustomerResponseDTO;
import com.bank.customer.infrastructure.adapter.web.dto.CustomerUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST Controller for Customer operations
 * Endpoint: /api/v1/customers
 * F1: CRUD operations for customers
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management APIs")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Creates a new customer
     * POST /api/v1/customers
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new customer", description = "Creates a new customer with person information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Customer already exists")
    })
    public Mono<CustomerResponseDTO> createCustomer(@Valid @RequestBody final CustomerRequestDTO requestDTO) {
        log.info("REST request to create customer: {}", requestDTO.getName());
        return customerService.createCustomer(requestDTO);
    }

    /**
     * Gets a customer by ID
     * GET /api/v1/customers/{id}
     */
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public Mono<CustomerResponseDTO> getCustomerById(@PathVariable final Long id) {
        log.info("REST request to get customer by ID: {}", id);
        return customerService.getCustomerById(id);
    }

    /**
     * Gets a customer by identification
     * GET /api/v1/customers/identification/{identification}
     */
    @GetMapping(
            value = "/identification/{identification}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get customer by identification", description = "Retrieves a customer by their identification number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public Mono<CustomerResponseDTO> getCustomerByIdentification(@PathVariable final String identification) {
        log.info("REST request to get customer by identification: {}", identification);
        return customerService.getCustomerByIdentification(identification);
    }

    /**
     * Gets all customers
     * GET /api/v1/customers
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all customers", description = "Retrieves all customers in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    })
    public Flux<CustomerResponseDTO> getAllCustomers() {
        log.info("REST request to get all customers");
        return customerService.getAllCustomers();
    }

    /**
     * Updates a customer
     * PUT /api/v1/customers/{id}
     */
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a customer", description = "Updates an existing customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public Mono<CustomerResponseDTO> updateCustomer(
            @PathVariable final Long id,
            @Valid @RequestBody final CustomerUpdateDTO updateDTO) {
        log.info("REST request to update customer: {}", id);
        return customerService.updateCustomer(id, updateDTO);
    }

    /**
     * Deletes a customer (logical deletion)
     * DELETE /api/v1/customers/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a customer", description = "Deletes a customer (logical deletion)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public Mono<Void> deleteCustomer(@PathVariable final Long id) {
        log.info("REST request to delete customer: {}", id);
        return customerService.deleteCustomer(id);
    }

    /**
     * Validates if a customer exists and is active.
     * GET /api/v1/customers/{id}/validate
     *
     * @param id id customer
     * @return true or false
     */
    @GetMapping(
            value = "/{id}/validate",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Validate customer", description = "Validates if a customer exists and is active")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer validation completed"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public Mono<Boolean> validateCustomer(@PathVariable final Long id) {
        log.info("REST request to validate customer: {}", id);
        return customerService.validateCustomer(id);
    }
}
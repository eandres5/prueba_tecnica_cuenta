package com.bank.account.infrastructure.adapter.web.controller;

import com.bank.account.application.service.AccountService;
import com.bank.account.infrastructure.adapter.web.dto.AccountRequestDTO;
import com.bank.account.infrastructure.adapter.web.dto.AccountResponseDTO;
import com.bank.account.infrastructure.adapter.web.dto.AccountUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST Controller for Account operations
 * Endpoint: /api/v1/accounts
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management APIs")
public class AccountController {

    private final AccountService accountService;

    /**
     * Creates a new account
     * POST /api/v1/accounts
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new account", description = "Creates a new bank account for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Account already exists")
    })
    public Mono<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO requestDTO) {
        log.info("REST request to create account: {}", requestDTO.getAccountNumber());
        return accountService.createAccount(requestDTO);
    }

    /**
     * Gets an account by ID
     * GET /api/v1/accounts/{id}
     */
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get account by ID", description = "Retrieves an account by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public Mono<AccountResponseDTO> getAccountById(@PathVariable Long id) {
        log.info("REST request to get account by ID: {}", id);
        return accountService.getAccountById(id);
    }

    /**
     * Gets an account by account number
     * GET /api/v1/accounts/number/{accountNumber}
     */
    @GetMapping(
            value = "/number/{accountNumber}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get account by number", description = "Retrieves an account by its account number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public Mono<AccountResponseDTO> getAccountByNumber(@PathVariable String accountNumber) {
        log.info("REST request to get account by number: {}", accountNumber);
        return accountService.getAccountByNumber(accountNumber);
    }

    /**
     * Gets all accounts for a customer
     * GET /api/v1/accounts/customer/{customerId}
     */
    @GetMapping(
            value = "/customer/{customerId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get accounts by customer", description = "Retrieves all accounts for a specific customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully")
    })
    public Flux<AccountResponseDTO> getAccountsByCustomerId(@PathVariable Long customerId) {
        log.info("REST request to get accounts for customer: {}", customerId);
        return accountService.getAccountsByCustomerId(customerId);
    }

    /**
     * Gets all accounts
     * GET /api/v1/accounts
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all accounts", description = "Retrieves all accounts in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully")
    })
    public Flux<AccountResponseDTO> getAllAccounts() {
        log.info("REST request to get all accounts");
        return accountService.getAllAccounts();
    }

    /**
     * Updates an account
     * PUT /api/v1/accounts/{id}
     */
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an account", description = "Updates an existing account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public Mono<AccountResponseDTO> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountUpdateDTO updateDTO) {
        log.info("REST request to update account: {}", id);
        return accountService.updateAccount(id, updateDTO);
    }

    /**
     * Deletes an account (logical deletion)
     * DELETE /api/v1/accounts/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an account", description = "Deletes an account (logical deletion)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public Mono<Void> deleteAccount(@PathVariable Long id) {
        log.info("REST request to delete account: {}", id);
        return accountService.deleteAccount(id);
    }
}
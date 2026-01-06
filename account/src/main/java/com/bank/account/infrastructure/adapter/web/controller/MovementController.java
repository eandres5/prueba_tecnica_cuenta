package com.bank.account.infrastructure.adapter.web.controller;

import com.bank.account.application.service.MovementService;
import com.bank.account.infrastructure.adapter.web.dto.MovementRequestDTO;
import com.bank.account.infrastructure.adapter.web.dto.MovementResponseDTO;
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
 * REST Controller for Movement operations
 * Endpoint: /api/v1/movements
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
@Tag(name = "Movements", description = "Movement management APIs")
public class MovementController {

    private final MovementService movementService;

    /**
     * Creates a new movement (CREDIT or DEBIT)
     * POST /api/v1/movements
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new movement",
            description = "Creates a new movement (CREDIT/DEBIT) with balance validation"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movement created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient balance"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public Mono<MovementResponseDTO> createMovement(@Valid @RequestBody MovementRequestDTO requestDTO) {
        log.info("REST request to create movement for account: {}", requestDTO.getAccountId());
        return movementService.createMovement(requestDTO);
    }

    /**
     * Gets a movement by ID
     * GET /api/v1/movements/{id}
     */
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get movement by ID", description = "Retrieves a movement by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movement found"),
            @ApiResponse(responseCode = "404", description = "Movement not found")
    })
    public Mono<MovementResponseDTO> getMovementById(@PathVariable Long id) {
        log.info("REST request to get movement by ID: {}", id);
        return movementService.getMovementById(id);
    }

    /**
     * Gets all movements for an account
     * GET /api/v1/movements/account/{accountId}
     */
    @GetMapping(
            value = "/account/{accountId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get movements by account", description = "Retrieves all movements for a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movements retrieved successfully")
    })
    public Flux<MovementResponseDTO> getMovementsByAccountId(@PathVariable Long accountId) {
        log.info("REST request to get movements for account: {}", accountId);
        return movementService.getMovementsByAccountId(accountId);
    }

    /**
     * Gets all movements
     * GET /api/v1/movements
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all movements", description = "Retrieves all movements in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movements retrieved successfully")
    })
    public Flux<MovementResponseDTO> getAllMovements() {
        log.info("REST request to get all movements");
        return movementService.getAllMovements();
    }

    /**
     * Updates a movement
     * PUT /api/v1/movements/{id}
     */
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a movement", description = "Updates an existing movement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movement updated successfully"),
            @ApiResponse(responseCode = "404", description = "Movement not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient balance")
    })
    public Mono<MovementResponseDTO> updateMovement(
            @PathVariable Long id,
            @Valid @RequestBody MovementRequestDTO updateDTO) {
        log.info("REST request to update movement: {}", id);
        return movementService.updateMovement(id, updateDTO);
    }

    /**
     * Deletes a movement (reverting balance)
     * DELETE /api/v1/movements/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a movement",
            description = "Deletes a movement and reverts the account balance"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movement deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movement not found")
    })
    public Mono<Void> deleteMovement(@PathVariable Long id) {
        log.info("REST request to delete movement: {}", id);
        return movementService.deleteMovement(id);
    }
}
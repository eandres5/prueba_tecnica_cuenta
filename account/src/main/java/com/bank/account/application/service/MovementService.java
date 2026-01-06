package com.bank.account.application.service;

import com.bank.account.application.mapper.MovementMapper;
import com.bank.account.domain.exception.BusinessValidationException;
import com.bank.account.domain.exception.InsufficientBalanceException;
import com.bank.account.domain.exception.MovementNotFoundException;
import com.bank.account.domain.model.Account;
import com.bank.account.domain.model.Movement;
import com.bank.account.domain.repository.AccountRepository;
import com.bank.account.domain.repository.MovementRepository;
import com.bank.account.infrastructure.adapter.web.dto.MovementRequestDTO;
import com.bank.account.infrastructure.adapter.web.dto.MovementResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service class for Movement business logic.
 * Manages financial transactions, ensures balance consistency,
 * and handles the logic for credits and debits.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MovementService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final MovementMapper movementMapper;
    private final AccountService accountService;

    /**
     * Creates a new financial movement (CREDIT or DEBIT).
     * Includes validations for positive amounts and sufficient funds for debits.
     * * @param requestDTO The movement data.
     *
     * @return A Mono emitting the processed movement response.
     * @throws BusinessValidationException If the amount is zero or negative.
     */
    @Transactional
    public Mono<MovementResponseDTO> createMovement(final MovementRequestDTO requestDTO) {
        log.info("Creating movement for account: {}, type: {}, amount: {}",
                requestDTO.getAccountId(), requestDTO.getMovementType(), requestDTO.getAmount());

        if (requestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new BusinessValidationException(
                    "Movement amount must be greater than zero"));
        }
        return accountService.getAccountEntity(requestDTO.getAccountId())
                .flatMap(account -> processMovement(account, requestDTO))
                .doOnSuccess(response -> log.info("Movement created successfully: {}", response.getMovementId()))
                .doOnError(error -> log.error("Error creating movement: {}", error.getMessage()));
    }

    /**
     * Logic to calculate the new balance and persist both the movement and the updated account.
     * * @param account The domain account entity.
     *
     * @param requestDTO The movement request.
     * @return A Mono emitting the saved movement DTO.
     * @throws InsufficientBalanceException If a DEBIT exceeds the current balance.
     */
    private Mono<MovementResponseDTO> processMovement(final Account account, final MovementRequestDTO requestDTO) {
        BigDecimal currentBalance = account.getCurrentBalance();
        BigDecimal newBalance;

        if ("DEBIT".equalsIgnoreCase(requestDTO.getMovementType())) {
            if (currentBalance.compareTo(requestDTO.getAmount()) < 0) {
                log.error("Insufficient balance. Current: {}, Required: {}",
                        currentBalance, requestDTO.getAmount());
                return Mono.error(new InsufficientBalanceException());
            }
            newBalance = currentBalance.subtract(requestDTO.getAmount());
        } else if ("CREDIT".equalsIgnoreCase(requestDTO.getMovementType())) {
            newBalance = currentBalance.add(requestDTO.getAmount());
        } else {
            return Mono.error(new BusinessValidationException(
                    "Invalid movement type. Must be CREDIT or DEBIT"));
        }

        Movement movement = movementMapper.toEntity(requestDTO);
        movement.setBalance(newBalance);

        return movementRepository.save(movement)
                .flatMap(savedMovement -> {
                    account.setCurrentBalance(newBalance);
                    return accountRepository.save(account)
                            .thenReturn(savedMovement);
                })
                .map(savedMovement -> {
                    MovementResponseDTO response = movementMapper.toResponseDTO(savedMovement);
                    response.setAccountNumber(account.getAccountNumber());
                    return response;
                });
    }

    /**
     * Retrieves a movement by its ID and enriches it with the account number.
     * * @param movementId Internal ID of the movement.
     *
     * @return A Mono emitting the found movement.
     */
    public Mono<MovementResponseDTO> getMovementById(final Long movementId) {
        log.info("Fetching movement with ID: {}", movementId);

        return movementRepository.findById(movementId)
                .switchIfEmpty(Mono.error(new MovementNotFoundException(movementId)))
                .flatMap(this::enrichMovementWithAccountNumber)
                .doOnSuccess(response -> log.info("Movement found: {}", response.getMovementId()))
                .doOnError(error -> log.error("Error fetching movement: {}", error.getMessage()));
    }

    /**
     * Retrieves all movements linked to a specific account.
     * * @param accountId Internal ID of the account.
     *
     * @return A Flux of movement responses.
     */
    public Flux<MovementResponseDTO> getMovementsByAccountId(final Long accountId) {
        log.info("Fetching movements for account: {}", accountId);

        return movementRepository.findByAccountId(accountId)
                .flatMap(this::enrichMovementWithAccountNumber)
                .doOnComplete(() -> log.info("Movements fetched for account: {}", accountId))
                .doOnError(error -> log.error("Error fetching movements: {}", error.getMessage()));
    }

    /**
     * Retrieves all movements in the system.
     * * @return A Flux of all movements.
     */
    public Flux<MovementResponseDTO> getAllMovements() {
        log.info("Fetching all movements");

        return movementRepository.findAll()
                .flatMap(this::enrichMovementWithAccountNumber)
                .doOnComplete(() -> log.info("All movements fetched"))
                .doOnError(error -> log.error("Error fetching all movements: {}", error.getMessage()));
    }

    /**
     * Updates an existing movement, reverting the previous impact on the balance
     * and applying the new amount/type.
     * * @param movementId ID of the movement to update.
     *
     * @param updateDTO New movement data.
     * @return A Mono emitting the updated movement response.
     */
    @Transactional
    public Mono<MovementResponseDTO> updateMovement(final Long movementId, final MovementRequestDTO updateDTO) {
        log.info("Updating movement with ID: {}", movementId);

        return movementRepository.findById(movementId)
                .switchIfEmpty(Mono.error(new MovementNotFoundException(movementId)))
                .flatMap(movement -> {
                    // Revertir el saldo anterior
                    return accountRepository.findById(movement.getAccountId())
                            .flatMap(account -> {
                                BigDecimal revertedBalance = revertBalance(
                                        account.getCurrentBalance(),
                                        movement.getAmount(),
                                        movement.getMovementType()
                                );
                                account.setCurrentBalance(revertedBalance);

                                // Aplicar nuevo movimiento
                                movement.setMovementType(updateDTO.getMovementType());
                                movement.setAmount(updateDTO.getAmount());

                                return processMovementUpdate(account, movement, updateDTO);
                            });
                })
                .doOnSuccess(response -> log.info("Movement updated successfully: {}", response.getMovementId()))
                .doOnError(error -> log.error("Error updating movement: {}", error.getMessage()));
    }

    /**
     * Logically deletes a movement and reverts its impact on the account balance.
     * * @param movementId ID of the movement to delete.
     *
     * @return A Mono signifying completion.
     */
    @Transactional
    public Mono<Void> deleteMovement(final Long movementId) {
        log.info("Deleting movement with ID: {}", movementId);

        return movementRepository.findById(movementId)
                .switchIfEmpty(Mono.error(new MovementNotFoundException(movementId)))
                .flatMap(movement ->
                        accountRepository.findById(movement.getAccountId())
                                .flatMap(account -> {
                                    // Revertir el saldo
                                    BigDecimal revertedBalance = revertBalance(
                                            account.getCurrentBalance(),
                                            movement.getAmount(),
                                            movement.getMovementType()
                                    );
                                    account.setCurrentBalance(revertedBalance);

                                    return accountRepository.save(account)
                                            .then(movementRepository.deleteById(movementId));
                                })
                )
                .doOnSuccess(v -> log.info("Movement deleted successfully: {}", movementId))
                .doOnError(error -> log.error("Error deleting movement: {}", error.getMessage()));
    }

    /**
     * Fetches movements for reporting based on customer and date range.
     * * @param customerId Internal customer ID.
     *
     * @param startDate Start of the period.
     * @param endDate   End of the period.
     * @return A Flux of raw Movement entities.
     */
    public Flux<Movement> getMovementsByCustomerAndDateRange(final Long customerId, final LocalDateTime startDate, final LocalDateTime endDate) {
        log.info("Fetching movements for customer: {} between {} and {}", customerId, startDate, endDate);
        return movementRepository.findByCustomerIdAndDateRange(customerId, startDate, endDate);
    }

    /**
     * Helper to add the account number to a movement response DTO.
     */
    private Mono<MovementResponseDTO> enrichMovementWithAccountNumber(final Movement movement) {
        return accountRepository.findById(movement.getAccountId())
                .map(account -> {
                    MovementResponseDTO response = movementMapper.toResponseDTO(movement);
                    response.setAccountNumber(account.getAccountNumber());
                    return response;
                })
                .defaultIfEmpty(movementMapper.toResponseDTO(movement));
    }

    /**
     * Reverts a balance change based on the original movement type.
     */
    private BigDecimal revertBalance(final BigDecimal currentBalance, final BigDecimal amount, final String movementType) {
        if ("DEBIT".equalsIgnoreCase(movementType)) {
            return currentBalance.add(amount);
        } else {
            return currentBalance.subtract(amount);
        }
    }

    /**
     * Internal logic for handling updates to existing movements and recalibrating the account balance.
     */
    private Mono<MovementResponseDTO> processMovementUpdate(final Account account, final Movement movement, final MovementRequestDTO updateDTO) {

        BigDecimal newBalance;
        if ("DEBIT".equalsIgnoreCase(updateDTO.getMovementType())) {
            if (account.getCurrentBalance().compareTo(updateDTO.getAmount()) < 0) {
                return Mono.error(new InsufficientBalanceException());
            }
            newBalance = account.getCurrentBalance().subtract(updateDTO.getAmount());
        } else {
            newBalance = account.getCurrentBalance().add(updateDTO.getAmount());
        }

        movement.setBalance(newBalance);
        account.setCurrentBalance(newBalance);

        return movementRepository.save(movement)
                .flatMap(savedMovement ->
                        accountRepository.save(account)
                                .thenReturn(savedMovement)
                )
                .map(savedMovement -> {
                    MovementResponseDTO response = movementMapper.toResponseDTO(savedMovement);
                    response.setAccountNumber(account.getAccountNumber());
                    return response;
                });
    }
}
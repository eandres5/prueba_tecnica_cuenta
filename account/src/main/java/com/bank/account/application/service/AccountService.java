package com.bank.account.application.service;

import com.bank.account.application.mapper.AccountMapper;
import com.bank.account.domain.exception.AccountAlreadyExistsException;
import com.bank.account.domain.exception.AccountNotFoundException;
import com.bank.account.domain.model.Account;
import com.bank.account.domain.repository.AccountRepository;
import com.bank.account.infrastructure.adapter.client.CustomerClient;
import com.bank.account.infrastructure.adapter.web.dto.AccountRequestDTO;
import com.bank.account.infrastructure.adapter.web.dto.AccountResponseDTO;
import com.bank.account.infrastructure.adapter.web.dto.AccountUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class for Account business logic.
 * Handles CRUD operations, cross-service validations, and domain rules for bank accounts.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CustomerClient customerClient;

    /**
     * Creates a new account in the system.
     * Validates customer existence via external client and ensures the account number is unique.
     *
     * @param requestDTO Data for the new account.
     * @return A Mono emitting the created account as a DTO.
     * @throws AccountAlreadyExistsException If the account number is already registered.
     */
    @Transactional
    public Mono<AccountResponseDTO> createAccount(final AccountRequestDTO requestDTO) {
        log.info("Creating new account for customer: {}", requestDTO.getCustomerId());

        return customerClient.validateCustomer(requestDTO.getCustomerId())
                .flatMap(isValid -> accountRepository.existsByAccountNumber(requestDTO.getAccountNumber()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new AccountAlreadyExistsException(requestDTO.getAccountNumber()));
                    }

                    Account account = accountMapper.toEntity(requestDTO);
                    return accountRepository.save(account);
                })
                .map(accountMapper::toResponseDTO)
                .doOnSuccess(response -> log.info("Account created successfully: {}", response.getAccountNumber()))
                .doOnError(error -> log.error("Error creating account: {}", error.getMessage()));
    }

    /**
     * Retrieves a specific account by its primary internal identifier.
     *
     * @param accountId The internal ID of the account.
     * @return A Mono emitting the found account DTO.
     * @throws AccountNotFoundException If no account matches the given ID.
     */
    public Mono<AccountResponseDTO> getAccountById(final Long accountId) {
        log.info("Fetching account with ID: {}", accountId);

        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
                .map(accountMapper::toResponseDTO)
                .doOnSuccess(response -> log.info("Account found: {}", response.getAccountNumber()))
                .doOnError(error -> log.error("Error fetching account: {}", error.getMessage()));
    }

    /**
     * Retrieves an account using its public account number.
     *
     * @param accountNumber The unique string account number.
     * @return A Mono emitting the found account DTO.
     * @throws AccountNotFoundException If the account number does not exist.
     */
    public Mono<AccountResponseDTO> getAccountByNumber(final String accountNumber) {
        log.info("Fetching account with number: {}", accountNumber);

        return accountRepository.findByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountNumber, true)))
                .map(accountMapper::toResponseDTO)
                .doOnSuccess(response -> log.info("Account found: {}", response.getAccountNumber()))
                .doOnError(error -> log.error("Error fetching account: {}", error.getMessage()));
    }

    /**
     * Retrieves all accounts associated with a specific customer.
     *
     * @param customerId The internal ID of the customer.
     * @return A Flux emitting all account DTOs belonging to the customer.
     */
    public Flux<AccountResponseDTO> getAccountsByCustomerId(final Long customerId) {
        log.info("Fetching accounts for customer: {}", customerId);

        return accountRepository.findByCustomerId(customerId)
                .map(accountMapper::toResponseDTO)
                .doOnComplete(() -> log.info("Accounts fetched for customer: {}", customerId))
                .doOnError(error -> log.error("Error fetching accounts: {}", error.getMessage()));
    }

    /**
     * Retrieves every account registered in the system.
     *
     * @return A Flux emitting all account DTOs.
     */
    public Flux<AccountResponseDTO> getAllAccounts() {
        log.info("Fetching all accounts");

        return accountRepository.findAll()
                .map(accountMapper::toResponseDTO)
                .doOnComplete(() -> log.info("All accounts fetched"))
                .doOnError(error -> log.error("Error fetching all accounts: {}", error.getMessage()));
    }

    /**
     * Updates an existing account's details.
     *
     * @param accountId The internal ID of the account to update.
     * @param updateDTO DTO containing the modified account fields.
     * @return A Mono emitting the updated account DTO.
     * @throws AccountNotFoundException If the account ID does not exist.
     */
    @Transactional
    public Mono<AccountResponseDTO> updateAccount(final Long accountId, final AccountUpdateDTO updateDTO) {
        log.info("Updating account with ID: {}", accountId);

        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
                .flatMap(account -> {
                    accountMapper.updateFromDto(updateDTO, account);
                    return accountRepository.save(account);
                })
                .map(accountMapper::toResponseDTO)
                .doOnSuccess(response -> log.info("Account updated successfully: {}", response.getAccountNumber()))
                .doOnError(error -> log.error("Error updating account: {}", error.getMessage()));
    }

    /**
     * Performs a logical deletion of an account.
     * Instead of removing the record, sets the status flag to false.
     *
     * @param accountId The internal ID of the account to be logically deleted.
     * @return A Mono that completes when the status has been updated.
     * @throws AccountNotFoundException If the account ID is not found.
     */
    @Transactional
    public Mono<Void> deleteAccount(final Long accountId) {
        log.info("Deleting account with ID: {}", accountId);

        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
                .flatMap(account -> {
                    account.setStatus(false);
                    return accountRepository.save(account);
                })
                .then()
                .doOnSuccess(v -> log.info("Account deleted successfully: {}", accountId))
                .doOnError(error -> log.error("Error deleting account: {}", error.getMessage()));
    }

    /**
     * Internal helper to retrieve the domain entity of an account.
     * Useful for operations that require the full entity instead of a DTO.
     *
     * @param accountId The internal ID of the account.
     * @return A Mono emitting the Account domain entity.
     * @throws AccountNotFoundException If the account ID is not found.
     */
    public Mono<Account> getAccountEntity(final Long accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)));
    }
}
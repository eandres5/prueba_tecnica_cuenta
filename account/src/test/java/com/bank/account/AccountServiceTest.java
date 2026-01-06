package com.bank.account;

import com.bank.account.application.mapper.AccountMapper;
import com.bank.account.application.service.AccountService;
import com.bank.account.domain.exception.AccountAlreadyExistsException;
import com.bank.account.domain.exception.AccountNotFoundException;
import com.bank.account.domain.model.Account;
import com.bank.account.domain.repository.AccountRepository;
import com.bank.account.infrastructure.adapter.client.CustomerClient;
import com.bank.account.infrastructure.adapter.web.dto.AccountRequestDTO;
import com.bank.account.infrastructure.adapter.web.dto.AccountResponseDTO;
import com.bank.account.infrastructure.adapter.web.dto.AccountUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Unit Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private AccountRequestDTO testRequest;
    private AccountResponseDTO testResponse;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .accountId(1L)
                .accountNumber("478758")
                .accountType("Ahorro")
                .initialBalance(new BigDecimal("2000"))
                .currentBalance(new BigDecimal("2000"))
                .status(true)
                .customerId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        testRequest = AccountRequestDTO.builder()
                .accountNumber("478758")
                .accountType("Ahorro")
                .initialBalance(new BigDecimal("2000"))
                .status(true)
                .customerId(1L)
                .build();

        testResponse = AccountResponseDTO.builder()
                .accountId(1L)
                .accountNumber("478758")
                .accountType("Ahorro")
                .initialBalance(new BigDecimal("2000"))
                .currentBalance(new BigDecimal("2000"))
                .status(true)
                .customerId(1L)
                .build();
    }

    @Test
    @DisplayName("Should create account successfully when customer is valid")
    void testCreateAccount_Success() {
        // Given
        when(customerClient.validateCustomer(anyLong())).thenReturn(Mono.just(true));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(Mono.just(false));
        when(accountMapper.toEntity(any(AccountRequestDTO.class))).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(testAccount));
        when(accountMapper.toResponseDTO(any(Account.class))).thenReturn(testResponse);

        // When
        Mono<AccountResponseDTO> result = accountService.createAccount(testRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getAccountNumber().equals("478758") &&
                                response.getAccountType().equals("Ahorro")
                )
                .verifyComplete();

        verify(customerClient, times(1)).validateCustomer(1L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when account number already exists")
    void testCreateAccount_AlreadyExists() {
        // Given
        when(customerClient.validateCustomer(anyLong())).thenReturn(Mono.just(true));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(Mono.just(true));

        // When
        Mono<AccountResponseDTO> result = accountService.createAccount(testRequest);

        // Then
        StepVerifier.create(result)
                .expectError(AccountAlreadyExistsException.class)
                .verify();

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should get account by ID successfully")
    void testGetAccountById_Success() {
        // Given
        when(accountRepository.findById(anyLong())).thenReturn(Mono.just(testAccount));
        when(accountMapper.toResponseDTO(any(Account.class))).thenReturn(testResponse);

        // When
        Mono<AccountResponseDTO> result = accountService.getAccountById(1L);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getAccountId().equals(1L))
                .verifyComplete();

        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    void testGetAccountById_NotFound() {
        // Given
        when(accountRepository.findById(anyLong())).thenReturn(Mono.empty());

        // When
        Mono<AccountResponseDTO> result = accountService.getAccountById(999L);

        // Then
        StepVerifier.create(result)
                .expectError(AccountNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should update account successfully")
    void testUpdateAccount_Success() {
        // Given
        AccountUpdateDTO updateDTO = AccountUpdateDTO.builder()
                .accountType("Corriente")
                .status(true)
                .build();

        when(accountRepository.findById(anyLong())).thenReturn(Mono.just(testAccount));
        doNothing().when(accountMapper).updateFromDto(any(AccountUpdateDTO.class), any(Account.class));
        when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(testAccount));
        when(accountMapper.toResponseDTO(any(Account.class))).thenReturn(testResponse);

        // When
        Mono<AccountResponseDTO> result = accountService.updateAccount(1L, updateDTO);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should get all accounts for a customer")
    void testGetAccountsByCustomerId_Success() {
        // Given
        when(accountRepository.findByCustomerId(anyLong())).thenReturn(Flux.just(testAccount));
        when(accountMapper.toResponseDTO(any(Account.class))).thenReturn(testResponse);

        // When
        Flux<AccountResponseDTO> result = accountService.getAccountsByCustomerId(1L);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(accountRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    @DisplayName("Should delete account (logical deletion)")
    void testDeleteAccount_Success() {
        // Given
        when(accountRepository.findById(anyLong())).thenReturn(Mono.just(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(testAccount));

        // When
        Mono<Void> result = accountService.deleteAccount(1L);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(accountRepository, times(1)).save(argThat(account ->
                account.getStatus().equals(false)
        ));
    }
}

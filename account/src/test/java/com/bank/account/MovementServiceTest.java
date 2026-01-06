package com.bank.account;

import com.bank.account.application.mapper.MovementMapper;
import com.bank.account.application.service.AccountService;
import com.bank.account.application.service.MovementService;
import com.bank.account.domain.model.Account;
import com.bank.account.domain.model.Movement;
import com.bank.account.domain.repository.AccountRepository;
import com.bank.account.domain.repository.MovementRepository;
import com.bank.account.infrastructure.adapter.web.dto.MovementRequestDTO;
import com.bank.account.infrastructure.adapter.web.dto.MovementResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MovementService
 * F5: Pruebas unitarias del servicio de movimientos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Movement Service Unit Tests")
class MovementServiceTest {

    @Mock
    private MovementRepository movementRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private MovementMapper movementMapper;
    @Mock
    private AccountService accountService;
    @InjectMocks
    private MovementService movementService;

    private Account testAccount;
    private Movement testMovement;
    private MovementRequestDTO testRequest;
    private MovementResponseDTO testResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
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

        testMovement = Movement.builder()
                .movementId(1L)
                .movementDate(LocalDateTime.now())
                .movementType("DEBIT")
                .amount(new BigDecimal("575"))
                .balance(new BigDecimal("1425"))
                .accountId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        testRequest = MovementRequestDTO.builder()
                .accountId(1L)
                .movementType("DEBIT")
                .amount(new BigDecimal("575"))
                .build();

        testResponse = MovementResponseDTO.builder()
                .movementId(1L)
                .movementDate(LocalDateTime.now())
                .movementType("DEBIT")
                .amount(new BigDecimal("575"))
                .balance(new BigDecimal("1425"))
                .accountId(1L)
                .accountNumber("478758")
                .build();
    }

    @Test
    @DisplayName("Should create DEBIT movement successfully with sufficient balance")
    void testCreateDebitMovement_Success() {
        // Given
        when(accountService.getAccountEntity(anyLong())).thenReturn(Mono.just(testAccount));
        when(movementMapper.toEntity(any(MovementRequestDTO.class))).thenReturn(testMovement);
        when(movementRepository.save(any(Movement.class))).thenReturn(Mono.just(testMovement));
        when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(testAccount));
        when(movementMapper.toResponseDTO(any(Movement.class))).thenReturn(testResponse);

        // When
        Mono<MovementResponseDTO> result = movementService.createMovement(testRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getMovementType().equals("DEBIT") &&
                                response.getAmount().compareTo(new BigDecimal("575")) == 0 &&
                                response.getBalance().compareTo(new BigDecimal("1425")) == 0
                )
                .verifyComplete();

        verify(movementRepository, times(1)).save(any(Movement.class));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should create CREDIT movement successfully")
    void testCreateCreditMovement_Success() {
        // Given
        testRequest.setMovementType("CREDIT");
        testRequest.setAmount(new BigDecimal("600"));

        testMovement.setMovementType("CREDIT");
        testMovement.setAmount(new BigDecimal("600"));
        testMovement.setBalance(new BigDecimal("2600"));

        when(accountService.getAccountEntity(anyLong())).thenReturn(Mono.just(testAccount));
        when(movementMapper.toEntity(any(MovementRequestDTO.class))).thenReturn(testMovement);
        when(movementRepository.save(any(Movement.class))).thenReturn(Mono.just(testMovement));
        when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(testAccount));
        when(movementMapper.toResponseDTO(any(Movement.class))).thenReturn(testResponse);

        // When
        Mono<MovementResponseDTO> result = movementService.createMovement(testRequest);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(movementRepository, times(1)).save(any(Movement.class));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when amount is zero or negative")
    void testCreateMovement_InvalidAmount() {
        // Given
        testRequest.setAmount(BigDecimal.ZERO);

        // When
        Mono<MovementResponseDTO> result = movementService.createMovement(testRequest);

        // Then
        StepVerifier.create(result)
                .expectError()
                .verify();
    }

    @Test
    @DisplayName("Should get movement by ID successfully")
    void testGetMovementById_Success() {
        // Given
        when(movementRepository.findById(anyLong())).thenReturn(Mono.just(testMovement));
        when(accountRepository.findById(anyLong())).thenReturn(Mono.just(testAccount));
        when(movementMapper.toResponseDTO(any(Movement.class))).thenReturn(testResponse);

        // When
        Mono<MovementResponseDTO> result = movementService.getMovementById(1L);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getMovementId().equals(1L) &&
                                response.getAccountNumber().equals("478758")
                )
                .verifyComplete();

        verify(movementRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should handle concurrent debit movements correctly")
    void testConcurrentDebitMovements() {
        // Given
        Account accountWithLowBalance = Account.builder()
                .accountId(1L)
                .accountNumber("478758")
                .currentBalance(new BigDecimal("100"))
                .build();

        testRequest.setAmount(new BigDecimal("60"));

        when(accountService.getAccountEntity(anyLong())).thenReturn(Mono.just(accountWithLowBalance));
        when(movementMapper.toEntity(any(MovementRequestDTO.class))).thenReturn(testMovement);
        when(movementRepository.save(any(Movement.class))).thenReturn(Mono.just(testMovement));
        when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(accountWithLowBalance));
        when(movementMapper.toResponseDTO(any(Movement.class))).thenReturn(testResponse);

        // When - First movement should succeed
        Mono<MovementResponseDTO> result1 = movementService.createMovement(testRequest);

        // Then
        StepVerifier.create(result1)
                .expectNextCount(1)
                .verifyComplete();
    }
}

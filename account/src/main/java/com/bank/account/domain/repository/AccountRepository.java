package com.bank.account.domain.repository;

import com.bank.account.domain.model.Account;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for Account entity operations with reactive support
 */
@Repository
public interface AccountRepository extends R2dbcRepository<Account, Long> {

    Mono<Account> findByAccountNumber(String accountNumber);

    Flux<Account> findByCustomerId(Long customerId);

    Mono<Boolean> existsByAccountNumber(String accountNumber);

    @Query("UPDATE accounts SET current_balance = :balance, updated_at = NOW() " +
            "WHERE account_id = :accountId")
    Mono<Void> updateBalance(Long accountId, java.math.BigDecimal balance);
}

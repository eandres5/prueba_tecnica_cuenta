package com.bank.account.domain.repository;

import com.bank.account.domain.model.Movement;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Repository
public interface MovementRepository extends R2dbcRepository<Movement, Long> {

    Flux<Movement> findByAccountId(Long accountId);

    @Query("SELECT * FROM movements WHERE account_id = :accountId " +
            "ORDER BY movement_date DESC LIMIT 1")
    Mono<Movement> findLatestByAccountId(Long accountId);

    @Query("SELECT m.* FROM movements m " +
            "INNER JOIN accounts a ON m.account_id = a.account_id " +
            "WHERE a.customer_id = :customerId " +
            "AND m.movement_date BETWEEN :startDate AND :endDate " +
            "ORDER BY m.movement_date DESC")
    Flux<Movement> findByCustomerIdAndDateRange(
            Long customerId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("SELECT m.* FROM movements m " +
            "WHERE m.account_id = :accountId " +
            "AND m.movement_date BETWEEN :startDate AND :endDate " +
            "ORDER BY m.movement_date DESC")
    Flux<Movement> findByAccountIdAndDateRange(
            Long accountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}

package com.bank.customer.domain.repository;

import com.bank.customer.domain.model.Person;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repository interface for Person entity operations
 */
@Repository
public interface PersonRepository extends R2dbcRepository<Person, Long> {
    Mono<Person> findByIdentification(String identification);
    Mono<Boolean> existsByIdentification(String identification);
}

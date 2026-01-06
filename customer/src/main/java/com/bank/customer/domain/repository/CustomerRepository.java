package com.bank.customer.domain.repository;

import com.bank.customer.domain.model.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for Customer entity operations with reactive support
 */
@Repository
public interface CustomerRepository extends R2dbcRepository<Customer, Long> {

    Mono<Customer> findByPersonId(Long personId);

    @Query("SELECT c.*, p.* FROM customers c " +
            "INNER JOIN persons p ON c.person_id = p.person_id " +
            "WHERE c.customer_id = :customerId")
    Mono<Customer> findCustomerWithPerson(Long customerId);

    @Query("SELECT c.*, p.* FROM customers c " +
            "INNER JOIN persons p ON c.person_id = p.person_id " +
            "WHERE p.identification = :identification")
    Mono<Customer> findByIdentification(String identification);

    @Query("SELECT c.*, p.* FROM customers c " +
            "INNER JOIN persons p ON c.person_id = p.person_id")
    Flux<Customer> findAllCustomersWithPersons();

    Mono<Boolean> existsByPersonId(Long personId);
}
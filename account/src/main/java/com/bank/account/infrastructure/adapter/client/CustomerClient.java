package com.bank.account.infrastructure.adapter.client;

import com.bank.account.domain.exception.CustomerValidationException;
import com.bank.account.infrastructure.adapter.web.dto.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Client for communicating with Customer Service via REST API
 * Validates customer existence and status before account operations
 */
@Slf4j
@Component
public class CustomerClient {

    private final WebClient webClient;

    public CustomerClient(@Value("${app.customer-service.base-url}") String baseUrl,
                          @Value("${app.customer-service.timeout:5000}") long timeout) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Validates if a customer exists and is active
     * @param customerId The customer ID to validate
     * @return Mono<Boolean> true if customer exists and is active
     */
    public Mono<Boolean> validateCustomer(Long customerId) {
        log.info("Validating customer with ID: {}", customerId);

        return webClient.get()
                .uri("/api/v1/customers/{id}", customerId)
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> Mono.error(new CustomerValidationException(
                                "Customer not found with ID: " + customerId))
                )
                .bodyToMono(CustomerDTO.class)
                .timeout(Duration.ofSeconds(5))
                .map(customer -> {
                    if (!customer.getStatus()) {
                        throw new CustomerValidationException(
                                "Customer is inactive with ID: " + customerId);
                    }
                    log.info("Customer validated successfully: {}", customerId);
                    return true;
                })
                .onErrorResume(ex -> {
                    if (ex instanceof CustomerValidationException) {
                        return Mono.error(ex);
                    }
                    log.error("Error validating customer {}: {}", customerId, ex.getMessage());
                    return Mono.error(new CustomerValidationException(
                            "Unable to validate customer with ID: " + customerId));
                });
    }

    /**
     * Gets customer details
     * @param customerId The customer ID
     * @return Mono<CustomerDTO> customer details
     */
    public Mono<CustomerDTO> getCustomer(Long customerId) {
        log.info("Fetching customer details for ID: {}", customerId);

        return webClient.get()
                .uri("/api/v1/customers/{id}", customerId)
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> Mono.error(new CustomerValidationException(
                                "Customer not found with ID: " + customerId))
                )
                .bodyToMono(CustomerDTO.class)
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(customer -> log.info("Customer details fetched: {}", customer.getName()))
                .onErrorResume(ex -> {
                    log.error("Error fetching customer {}: {}", customerId, ex.getMessage());
                    return Mono.error(new CustomerValidationException(
                            "Unable to fetch customer with ID: " + customerId));
                });
    }
}
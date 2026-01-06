
package com.bank.customer.application.service;

import com.bank.customer.application.mapper.CustomerMapper;
import com.bank.customer.domain.exception.CustomerNotFoundException;
import com.bank.customer.domain.model.Customer;
import com.bank.customer.domain.model.Person;
import com.bank.customer.domain.repository.CustomerRepository;
import com.bank.customer.domain.repository.PersonRepository;
import com.bank.customer.infrastructure.adapter.messaging.CustomerEventPublisher;
import com.bank.customer.infrastructure.adapter.web.dto.CustomerRequestDTO;
import com.bank.customer.infrastructure.adapter.web.dto.CustomerResponseDTO;
import com.bank.customer.infrastructure.adapter.web.dto.CustomerUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class for Customer business logic
 * Handles CRUD operations and business validations for customers
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PersonRepository personRepository;
    private final CustomerMapper customerMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerEventPublisher eventPublisher;

    /**
     * Creates a new customer with person information
     * F1: Create operation
     */
    @Transactional
    public Mono<CustomerResponseDTO> createCustomer(CustomerRequestDTO requestDTO) {
        log.info("Creating new customer with identification: {}", requestDTO.getIdentification());

        return personRepository.existsByIdentification(requestDTO.getIdentification())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(
                                new IllegalStateException("Person already exists with identification " +
                                        requestDTO.getIdentification()));
                    }

                    Person person = customerMapper.toPersonEntity(requestDTO);
                    return personRepository.save(person);
                })
                .flatMap(savedPerson -> {
                    Customer customer = customerMapper.toCustomerEntity(
                            requestDTO, savedPerson.getPersonId());

                    customer.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

                    return customerRepository.save(customer)
                            .map(savedCustomer ->
                                    customerMapper.toResponseDTO(savedPerson, savedCustomer)
                            );
                })
                .doOnSuccess(response -> {
                    log.info("Customer created successfully with ID: {}", response.getCustomerId());
                    eventPublisher.publishCustomerCreatedEvent(response.getCustomerId(), response.getName());
                })
                .doOnError(error ->
                        log.error("Error creating customer: {}", error.getMessage()));
    }

    /**
     * Gets a customer by ID
     * F1: Read operation
     */
    public Mono<CustomerResponseDTO> getCustomerById(Long customerId) {
        log.info("Fetching customer with ID: {}", customerId);

        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(customerId)))
                .flatMap(customer ->
                        personRepository.findById(customer.getPersonId())
                                .map(person ->
                                        customerMapper.toResponseDTO(person, customer)
                                )
                )
                .doOnSuccess(response ->
                        log.info("Customer found: {}", response.getName()))
                .doOnError(error ->
                        log.error("Error fetching customer: {}", error.getMessage()));
    }

    /**
     * Gets a customer by identification
     */
    public Mono<CustomerResponseDTO> getCustomerByIdentification(String identification) {
        log.info("Fetching customer with identification: {}", identification);

        return personRepository.findByIdentification(identification)
                .switchIfEmpty(Mono.error(
                        new CustomerNotFoundException(
                                "Customer not found with identification: " + identification)))
                .flatMap(person ->
                        customerRepository.findByPersonId(person.getPersonId())
                                .switchIfEmpty(Mono.error(
                                        new CustomerNotFoundException(
                                                "Customer not found for personId: " + person.getPersonId())))
                                .map(customer ->
                                        customerMapper.toResponseDTO(person, customer)
                                )
                )
                .doOnSuccess(response ->
                        log.info("Customer found: {}", response.getName()))
                .doOnError(error ->
                        log.error("Error fetching customer: {}", error.getMessage()));
    }

    /**
     * Gets all customers
     * F1: Read operation
     */
    public Flux<CustomerResponseDTO> getAllCustomers() {
        log.info("Fetching all customers");

        return customerRepository.findAll()
                .flatMap(customer ->
                        personRepository.findById(customer.getPersonId())
                                .map(person ->
                                        customerMapper.toResponseDTO(person, customer)
                                )
                )
                .doOnComplete(() -> log.info("All customers fetched"))
                .doOnError(error ->
                        log.error("Error fetching all customers: {}", error.getMessage()));
    }

    /**
     * Updates a customer
     * F1: Update operation
     */
    @Transactional
    public Mono<CustomerResponseDTO> updateCustomer(Long customerId, CustomerUpdateDTO updateDTO) {
        log.info("Updating customer with ID: {}", customerId);

        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(customerId)))
                .flatMap(customer ->
                        personRepository.findById(customer.getPersonId())
                                .flatMap(person -> {

                                    customerMapper.updatePersonFromDto(updateDTO, person);
                                    customerMapper.updateCustomerFromDto(updateDTO, customer);

                                    if (updateDTO.getPassword() != null &&
                                            !updateDTO.getPassword().isBlank()) {
                                        customer.setPassword(
                                                passwordEncoder.encode(updateDTO.getPassword()));
                                    }

                                    return personRepository.save(person)
                                            .then(customerRepository.save(customer))
                                            .map(savedCustomer ->
                                                    customerMapper.toResponseDTO(person, savedCustomer)
                                            );
                                })
                )
                .doOnSuccess(response -> {
                    log.info("Customer updated successfully: {}", response.getName());
                    eventPublisher.publishCustomerUpdatedEvent(
                            response.getCustomerId(), response.getName(), true);
                })
                .doOnError(error ->
                        log.error("Error updating customer: {}", error.getMessage()));
    }

    /**
     * Deletes a customer (logical deletion by setting status to false)
     * F1: Delete operation
     */
    @Transactional
    public Mono<Void> deleteCustomer(Long customerId) {
        log.info("Deleting customer with ID: {}", customerId);

        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(customerId)))
                .flatMap(customer -> {
                    customer.setStatus(false);
                    return customerRepository.save(customer);
                })
                .then()
                .doOnSuccess(v -> {
                    log.info("Customer deleted successfully: {}", customerId);
                    eventPublisher.publishCustomerDeletedEvent(customerId);
                })
                .doOnError(error -> log.error("Error deleting customer: {}", error.getMessage()));
    }

    /**
     * Validates if a customer exists and is active
     */
    public Mono<Boolean> validateCustomer(Long customerId) {
        log.info("Validating customer with ID: {}", customerId);

        return customerRepository.findById(customerId)
                .map(customer -> {
                    if (!customer.getStatus()) {
                        log.warn("Customer {} is inactive", customerId);
                        return false;
                    }
                    return true;
                })
                .defaultIfEmpty(false)
                .doOnSuccess(isValid -> log.info("Customer {} validation result: {}", customerId, isValid));
    }
}
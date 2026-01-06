package com.bank.account.infrastructure.adapter.messaging.listener;

import com.bank.account.domain.repository.AccountRepository;
import com.bank.account.infrastructure.adapter.web.dto.CustomerEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Listens for customer events from Customer Service
 * Processes customer lifecycle events asynchronously
 * Subscribes to Topic (Pub/Sub pattern)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {

    private final AccountRepository accountRepository;

    /**
     * Listens to customer events from the customer.events topic
     * Multiple subscribers can receive the same event
     */
    @JmsListener(
            destination = "${app.messaging.customer-events-topic}",
            containerFactory = "topicListenerContainerFactory"
    )
    public void handleCustomerEvent(CustomerEventDTO event) {
        log.info("Received customer event: {} for customer ID: {} at {}",
                event.getEventType(),
                event.getCustomerId(),
                event.getTimestamp());

        try {
            switch (event.getEventType()) {
                case "CUSTOMER_CREATED":
                    handleCustomerCreated(event);
                    break;

                case "CUSTOMER_UPDATED":
                    handleCustomerUpdated(event);
                    break;

                case "CUSTOMER_DELETED":
                    handleCustomerDeleted(event);
                    break;

                case "CUSTOMER_STATUS_CHANGED":
                    handleCustomerStatusChanged(event);
                    break;

                default:
                    log.warn("Unknown customer event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing customer event {}: {}",
                    event.getEventType(), e.getMessage(), e);
        }
    }

    /**
     * Handles customer created event
     */
    private void handleCustomerCreated(CustomerEventDTO event) {
        log.info("Processing CUSTOMER_CREATED: {} ({})",
                event.getCustomerName(),
                event.getCustomerId());

        // Business logic: Could initialize customer data, send welcome notifications, etc.
        log.info("Customer {} is now eligible to create accounts", event.getCustomerName());
    }

    /**
     * Handles customer updated event
     */
    private void handleCustomerUpdated(CustomerEventDTO event) {
        log.info("Processing CUSTOMER_UPDATED: {} ({})",
                event.getCustomerName(),
                event.getCustomerId());

        // Business logic: Update cached customer data, sync with other systems, etc.
        if (Boolean.FALSE.equals(event.getStatus())) {
            log.warn("Customer {} is now INACTIVE - Consider restricting account operations",
                    event.getCustomerId());
        }
    }

    /**
     * Handles customer deleted event
     */
    private void handleCustomerDeleted(CustomerEventDTO event) {
        log.info("Processing CUSTOMER_DELETED: Customer ID {}", event.getCustomerId());

        // Business logic: Archive accounts, cleanup data, freeze operations
        accountRepository.findByCustomerId(event.getCustomerId())
                .flatMap(account -> {
                    log.info("Freezing account {} due to customer deletion",
                            account.getAccountNumber());
                    account.setStatus(false);
                    return accountRepository.save(account);
                })
                .subscribe(
                        account -> log.info("Account {} frozen successfully", account.getAccountNumber()),
                        error -> log.error("Error freezing accounts: {}", error.getMessage())
                );
    }

    /**
     * Handles customer status changed event
     */
    private void handleCustomerStatusChanged(CustomerEventDTO event) {
        log.info("Processing CUSTOMER_STATUS_CHANGED: Customer {} - New Status: {}",
                event.getCustomerId(),
                event.getStatus() ? "ACTIVE" : "INACTIVE");

        // Business logic: Enable/disable account operations based on customer status
        if (Boolean.FALSE.equals(event.getStatus())) {
            // Customer became inactive - suspend accounts
            accountRepository.findByCustomerId(event.getCustomerId())
                    .doOnNext(account ->
                            log.warn("Account {} should be suspended (Customer INACTIVE)",
                                    account.getAccountNumber()))
                    .subscribe();
        } else {
            // Customer became active - reactivate accounts
            log.info("Customer {} reactivated - Accounts can be enabled", event.getCustomerId());
        }
    }
}

package com.bank.account.infrastructure.adapter.messaging.publisher;

import com.bank.account.infrastructure.adapter.web.dto.AccountEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Publisher for account events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventPublisher {

    private final JmsTemplate jmsTemplate;

    @Value("${app.messaging.account-events-topic}")
    private String accountEventsTopic;

    public void publishAccountCreatedEvent(Long accountId, String accountNumber,
                                           String accountType, Long customerId, BigDecimal balance) {
        AccountEventDTO event = AccountEventDTO.builder()
                .eventType("ACCOUNT_CREATED")
                .accountId(accountId)
                .accountNumber(accountNumber)
                .accountType(accountType)
                .currentBalance(balance)
                .customerId(customerId)
                .status(true)
                .timestamp(LocalDateTime.now())
                .build();

        publishEvent(event);
        log.info("Published ACCOUNT_CREATED event for account: {} (ID: {})", accountNumber, accountId);
    }

    public void publishAccountUpdatedEvent(Long accountId, String accountNumber, BigDecimal balance) {
        AccountEventDTO event = AccountEventDTO.builder()
                .eventType("ACCOUNT_UPDATED")
                .accountId(accountId)
                .accountNumber(accountNumber)
                .currentBalance(balance)
                .timestamp(LocalDateTime.now())
                .build();

        publishEvent(event);
        log.info("Published ACCOUNT_UPDATED event for account: {} (ID: {})", accountNumber, accountId);
    }

    public void publishAccountDeletedEvent(Long accountId, String accountNumber) {
        AccountEventDTO event = AccountEventDTO.builder()
                .eventType("ACCOUNT_DELETED")
                .accountId(accountId)
                .accountNumber(accountNumber)
                .status(false)
                .timestamp(LocalDateTime.now())
                .build();

        publishEvent(event);
        log.info("Published ACCOUNT_DELETED event for account: {} (ID: {})", accountNumber, accountId);
    }

    private void publishEvent(AccountEventDTO event) {
        try {
            jmsTemplate.setPubSubDomain(true); // Use Topic
            jmsTemplate.convertAndSend(accountEventsTopic, event, message -> {
                message.setStringProperty("eventType", event.getEventType());
                message.setLongProperty("accountId", event.getAccountId());
                return message;
            });
            log.debug("Event sent to topic '{}': {}", accountEventsTopic, event.getEventType());
        } catch (Exception e) {
            log.error("Error publishing account event: {} - Error: {}",
                    event.getEventType(), e.getMessage(), e);
        }
    }
}

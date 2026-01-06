package com.bank.account.infrastructure.adapter.messaging.publisher;

import com.bank.account.infrastructure.adapter.web.dto.MovementEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Publisher for movement events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MovementEventPublisher {

    private final JmsTemplate jmsTemplate;

    @Value("${app.messaging.movement-events-topic}")
    private String movementEventsTopic;

    public void publishMovementCreatedEvent(Long movementId, Long accountId, String accountNumber,
                                            String movementType, BigDecimal amount,
                                            BigDecimal balanceBefore, BigDecimal balanceAfter,
                                            Long customerId) {
        MovementEventDTO event = MovementEventDTO.builder()
                .eventType("MOVEMENT_CREATED")
                .movementId(movementId)
                .accountId(accountId)
                .accountNumber(accountNumber)
                .movementType(movementType)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .customerId(customerId)
                .timestamp(LocalDateTime.now())
                .build();

        publishEvent(event);
        log.info("✅ Published MOVEMENT_CREATED event: {} {} on account {}",
                movementType, amount, accountNumber);
    }

    private void publishEvent(MovementEventDTO event) {
        try {
            jmsTemplate.setPubSubDomain(true); // Use Topic
            jmsTemplate.convertAndSend(movementEventsTopic, event, message -> {
                message.setStringProperty("eventType", event.getEventType());
                message.setStringProperty("movementType", event.getMovementType());
                message.setLongProperty("accountId", event.getAccountId());
                return message;
            });
            log.debug("Event sent to topic '{}': {}", movementEventsTopic, event.getEventType());
        } catch (Exception e) {
            log.error("Error publishing movement event: {} - Error: {}",
                    event.getEventType(), e.getMessage(), e);
        }
    }
}

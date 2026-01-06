package com.bank.customer.infrastructure.adapter.messaging;

import com.bank.customer.infrastructure.adapter.web.dto.CustomerEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Publisher for customer events to ActiveMQ
 * Sends notifications when customer operations occur
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventPublisher {

    private final JmsTemplate jmsTemplate;

    @Value("${app.messaging.customer-events-topic}")
    private String customerEventsTopic;

    /**
     * Publishes a customer created event
     */
    public void publishCustomerCreatedEvent(Long customerId, String customerName) {
        CustomerEventDTO event = CustomerEventDTO.builder()
                .eventType("CUSTOMER_CREATED")
                .customerId(customerId)
                .customerName(customerName)
                .status(true)
                .timestamp(LocalDateTime.now())
                .build();

        publishEvent(event);
        log.info("Published CUSTOMER_CREATED event for customer: {} (ID: {})", customerName, customerId);
    }

    /**
     * Publishes a customer updated event
     */
    public void publishCustomerUpdatedEvent(Long customerId, String customerName, Boolean status) {
        CustomerEventDTO event = CustomerEventDTO.builder()
                .eventType("CUSTOMER_UPDATED")
                .customerId(customerId)
                .customerName(customerName)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();

        publishEvent(event);
        log.info("Published CUSTOMER_UPDATED event for customer: {} (ID: {})", customerName, customerId);
    }

    /**
     * Publishes a customer deleted event
     */
    public void publishCustomerDeletedEvent(Long customerId) {
        CustomerEventDTO event = CustomerEventDTO.builder()
                .eventType("CUSTOMER_DELETED")
                .customerId(customerId)
                .status(false)
                .timestamp(LocalDateTime.now())
                .build();

        publishEvent(event);
        log.info("Published CUSTOMER_DELETED event for (ID: {})", customerId);
    }

    /**
     * Publishes a customer status changed event
     */
    public void publishCustomerStatusChangedEvent(Long customerId, String customerName, Boolean newStatus) {
        CustomerEventDTO event = CustomerEventDTO.builder()
                .eventType("CUSTOMER_STATUS_CHANGED")
                .customerId(customerId)
                .customerName(customerName)
                .status(newStatus)
                .timestamp(LocalDateTime.now())
                .build();

        publishEvent(event);
        log.info("Published CUSTOMER_STATUS_CHANGED event for customer: {} (ID: {}), New Status: {}",
                customerName, customerId, newStatus);
    }

    /**
     * Publishes an event to ActiveMQ topic
     */
    private void publishEvent(CustomerEventDTO event) {
        try {
            // Configure for Topic (Pub/Sub)
            jmsTemplate.setPubSubDomain(true);

            // Send message
            jmsTemplate.convertAndSend(customerEventsTopic, event, message -> {
                message.setStringProperty("eventType", event.getEventType());
                message.setLongProperty("customerId", event.getCustomerId());
                return message;
            });

            log.debug("Event sent to topic '{}': {}", customerEventsTopic, event.getEventType());

        } catch (Exception e) {
            log.error("Error publishing customer event: {} - Error: {}",
                    event.getEventType(), e.getMessage(), e);
        }
    }

}

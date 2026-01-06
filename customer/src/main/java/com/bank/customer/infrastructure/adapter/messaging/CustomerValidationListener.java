package com.bank.customer.infrastructure.adapter.messaging;


import com.bank.customer.application.service.CustomerService;
import com.bank.customer.infrastructure.adapter.web.dto.CustomerValidationRequest;
import com.bank.customer.infrastructure.adapter.web.dto.CustomerValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Listens for customer validation requests from Account Service
 * Validates customer and sends response back
 * Uses Queue (Point-to-Point) pattern
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerValidationListener {

    private final CustomerService customerService;
    private final JmsTemplate jmsTemplate;

    /**
     * Listens to customer validation requests
     * Validates customer and sends response to reply queue
     */
    @JmsListener(
            destination = "${app.messaging.customer-validation-queue}",
            containerFactory = "jmsListenerContainerFactory"
    )
    public void handleValidationRequest(CustomerValidationRequest request) {
        log.info("Received validation request for customer ID: {} (Correlation: {})",
                request.getCustomerId(), request.getCorrelationId());

        try {
            // Validate customer using service
            customerService.getCustomerById(request.getCustomerId())
                    .subscribe(
                            customer -> {
                                // Customer found - send positive response
                                CustomerValidationResponse response = CustomerValidationResponse.builder()
                                        .correlationId(request.getCorrelationId())
                                        .customerId(customer.getCustomerId())
                                        .isValid(true)
                                        .isActive(customer.getStatus())
                                        .customerName(customer.getName())
                                        .message("Customer validation successful")
                                        .build();

                                sendValidationResponse(response);
                                log.info("Customer {} is valid and {}",
                                        customer.getCustomerId(),
                                        customer.getStatus() ? "ACTIVE" : "INACTIVE");
                            },
                            error -> {
                                // Customer not found - send negative response
                                CustomerValidationResponse response = CustomerValidationResponse.builder()
                                        .correlationId(request.getCorrelationId())
                                        .customerId(request.getCustomerId())
                                        .isValid(false)
                                        .isActive(false)
                                        .customerName(null)
                                        .message("Customer not found: " + error.getMessage())
                                        .build();

                                sendValidationResponse(response);
                                log.warn("Customer {} not found", request.getCustomerId());
                            }
                    );

        } catch (Exception e) {
            log.error("Error processing validation request: {}", e.getMessage(), e);

            // Send error response
            CustomerValidationResponse errorResponse = CustomerValidationResponse.builder()
                    .correlationId(request.getCorrelationId())
                    .customerId(request.getCustomerId())
                    .isValid(false)
                    .isActive(false)
                    .message("Validation error: " + e.getMessage())
                    .build();

            sendValidationResponse(errorResponse);
        }
    }

    /**
     * Sends validation response to response queue
     */
    private void sendValidationResponse(CustomerValidationResponse response) {
        try {
            jmsTemplate.setPubSubDomain(false); // Use Queue
            jmsTemplate.convertAndSend(
                    "customer.validation.response",
                    response,
                    message -> {
                        message.setStringProperty("correlationId", response.getCorrelationId());
                        return message;
                    }
            );
            log.debug("Sent validation response for correlation: {}", response.getCorrelationId());
        } catch (Exception e) {
            log.error("Error sending validation response: {}", e.getMessage(), e);
        }
    }
}

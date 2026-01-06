package com.bank.customer.domain.exception;

/**
 * Exception thrown when business validation fails
 */
public class BusinessValidationException extends RuntimeException {
    public BusinessValidationException(String message) {
        super(message);
    }
}
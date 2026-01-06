package com.bank.account.domain.exception;

public class CustomerValidationException extends RuntimeException {
    public CustomerValidationException(String message) {
        super(message);
    }

    public CustomerValidationException(Long customerId) {
        super("Customer not found or inactive with ID: " + customerId);
    }
}

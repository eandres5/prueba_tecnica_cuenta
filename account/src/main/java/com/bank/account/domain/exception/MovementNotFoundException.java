package com.bank.account.domain.exception;

/**
 * Exception thrown when a movement is not found
 */
public class MovementNotFoundException extends RuntimeException {
    public MovementNotFoundException(String message) {
        super(message);
    }

    public MovementNotFoundException(Long movementId) {
        super("Movement not found with ID: " + movementId);
    }
}

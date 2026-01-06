package com.bank.account.domain.exception;

/**
 * Exception thrown when insufficient balance for a transaction
 */
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException() {
        super("Saldo no disponible");
    }
}

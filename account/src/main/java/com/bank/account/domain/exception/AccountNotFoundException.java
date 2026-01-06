package com.bank.account.domain.exception;

/**
 * Exception thrown when an account is not found
 */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(Long accountId) {
        super("Account not found with ID: " + accountId);
    }

    public AccountNotFoundException(String accountNumber, boolean byNumber) {
        super("Account not found with number: " + accountNumber);
    }
}

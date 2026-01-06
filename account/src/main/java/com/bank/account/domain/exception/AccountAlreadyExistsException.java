package com.bank.account.domain.exception;

/**
 * Exception thrown when an account already exists
 */
public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(String accountNumber) {
        super("Account already exists with number: " + accountNumber);
    }
}

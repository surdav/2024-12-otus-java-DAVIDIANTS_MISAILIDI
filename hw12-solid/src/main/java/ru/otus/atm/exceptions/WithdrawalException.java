package ru.otus.atm.exceptions;

/**
 * Exception thrown when the ATM cannot dispense the requested amount
 */
public class WithdrawalException extends RuntimeException {
    public WithdrawalException(String message) {
        super(message);
    }
}

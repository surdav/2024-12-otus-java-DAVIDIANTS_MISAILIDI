package ru.otus.jdbc.exception;

public class NoSuitableConstructorException extends RuntimeException {

    public NoSuitableConstructorException(String message, Throwable cause) {
        super(message, cause);
    }
}

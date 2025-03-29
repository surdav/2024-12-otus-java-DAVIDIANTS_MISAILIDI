package ru.otus.jdbc.exception;

public class OrmMappingException extends RuntimeException {
    public OrmMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
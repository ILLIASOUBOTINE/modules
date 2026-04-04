package org.example.exception;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateException(String message) {
        super(message);
    }
}

package org.example.exception;

public class DatabaseException extends DaoException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(String message) {
        super(message);
    }
}

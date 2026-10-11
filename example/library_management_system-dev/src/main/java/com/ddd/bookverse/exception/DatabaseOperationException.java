package com.ddd.bookverse.exception;

import static com.ddd.bookverse.constants.Constants.EXCEPTION_DATABASE_OPERATION;

/**
 * Custom exception class for handling database operation errors.
 */
public class DatabaseOperationException extends RuntimeException {

    /**
     * Constructs a new DatabaseOperationException with a default error message.
     */
    public DatabaseOperationException() {
        super(EXCEPTION_DATABASE_OPERATION);
    }

    /**
     * Constructs a new DatabaseOperationException with the specified detail message.
     *
     * @param message The detail message.
     */
    public DatabaseOperationException(String message) {
        super(message);
    }
}
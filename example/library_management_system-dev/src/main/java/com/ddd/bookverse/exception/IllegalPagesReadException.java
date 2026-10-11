package com.ddd.bookverse.exception;

import static com.ddd.bookverse.constants.Constants.EXCEPTION_ILLEGALPAGESREAD;

/**
 * Custom exception class for handling illegal pages read errors.
 */
public class IllegalPagesReadException extends RuntimeException {

    /**
     * Constructs a new IllegalPagesReadException with a default error message.
     */
    public IllegalPagesReadException() {
        super(EXCEPTION_ILLEGALPAGESREAD);
    }

    /**
     * Constructs a new IllegalPagesReadException with the specified detail message.
     *
     * @param message The detail message.
     */
    public IllegalPagesReadException(String message) {
        super(message);
    }
}
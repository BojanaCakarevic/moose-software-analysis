package com.ddd.bookverse.exception;

import static com.ddd.bookverse.constants.Constants.EXCEPTION_OVER_DUE_BOOK;

/**
 * Custom exception class for handling overdue book errors.
 */
public class OverdueBookException extends RuntimeException {

    /**
     * Constructs a new OverdueBookException with a default error message.
     */
    public OverdueBookException() {
        super(EXCEPTION_OVER_DUE_BOOK);
    }

    /**
     * Constructs a new OverdueBookException with the specified detail message.
     *
     * @param message The detail message.
     */
    public OverdueBookException(String message) {
        super(message);
    }
}
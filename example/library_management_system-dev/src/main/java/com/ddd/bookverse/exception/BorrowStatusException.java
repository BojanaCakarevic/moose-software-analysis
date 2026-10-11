package com.ddd.bookverse.exception;

import static com.ddd.bookverse.constants.Constants.EXCEPTION_BORROW_STATUS;

/**
 * Custom exception class for handling borrow status errors.
 */
public class BorrowStatusException extends RuntimeException {

    /**
     * Constructs a new BorrowStatusException with a default error message.
     */
    public BorrowStatusException() {
        super(EXCEPTION_BORROW_STATUS);
    }

    /**
     * Constructs a new BorrowStatusException with the specified detail message.
     *
     * @param message The detail message.
     */
    public BorrowStatusException(String message) {
        super(message);
    }
}
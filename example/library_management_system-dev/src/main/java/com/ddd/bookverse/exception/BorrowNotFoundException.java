package com.ddd.bookverse.exception;

import static com.ddd.bookverse.constants.Constants.EXCEPTION_BORROW_NOT_FOUND;

/**
 * Custom exception class for handling borrow not found errors.
 */
public class BorrowNotFoundException extends RuntimeException {

    /**
     * Constructs a new BorrowNotFoundException with a default error message.
     */
    public BorrowNotFoundException() {
        super(EXCEPTION_BORROW_NOT_FOUND);
    }

    /**
     * Constructs a new BorrowNotFoundException with the specified detail message.
     *
     * @param message The detail message.
     */
    public BorrowNotFoundException(String message) {
        super(message);
    }
}
package com.ddd.bookverse.exception;

import static com.ddd.bookverse.constants.Constants.EXCEPTION_BOOK_NOT_FOUND;

/**
 * Custom exception class for handling book not found errors.
 */
public class BookNotFoundException extends RuntimeException {

    /**
     * Constructs a new BookNotFoundException with a default error message.
     */
    public BookNotFoundException() {
        super(EXCEPTION_BOOK_NOT_FOUND);
    }

    /**
     * Constructs a new BookNotFoundException with the specified detail message.
     *
     * @param message The detail message.
     */
    public BookNotFoundException(String message) {
        super(message);
    }
}
package com.ddd.bookverse.exception;

import static com.ddd.bookverse.constants.Constants.EXCEPTION_REVIEW_NOT_FOUND;

/**
 * Custom exception class for handling review not found errors.
 */
public class ReviewNotFoundException extends RuntimeException {

    /**
     * Constructs a new ReviewNotFoundException with a default error message.
     */
    public ReviewNotFoundException() {
        super(EXCEPTION_REVIEW_NOT_FOUND);
    }

    /**
     * Constructs a new ReviewNotFoundException with the specified detail message.
     *
     * @param message The detail message.
     */
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
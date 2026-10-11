package com.ddd.bookverse.exception;

import static com.ddd.bookverse.constants.Constants.EXCEPTION_USER_NOT_FOUND;

/**
 * Custom exception class for handling user not found errors.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with a default error message.
     */
    public UserNotFoundException() {
        super(EXCEPTION_USER_NOT_FOUND);
    }

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message The detail message.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
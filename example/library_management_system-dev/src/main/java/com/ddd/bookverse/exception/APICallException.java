package com.ddd.bookverse.exception;

import static com.ddd.bookverse.constants.Constants.EXCEPTION_CALL_API;

/**
 * Custom exception class for handling API call errors.
 */
public class APICallException extends RuntimeException {

    /**
     * Constructs a new APICallException with a default error message.
     */
    public APICallException() {
        super(EXCEPTION_CALL_API);
    }

    /**
     * Constructs a new APICallException with the specified detail message.
     *
     * @param message The detail message.
     */
    public APICallException(String message) {
        super(message);
    }
}
package com.imaginelearning.books.exception;

public class ExternalServiceBadResponseException extends RuntimeException {

    public ExternalServiceBadResponseException(String message) {
        super(message);
    }

    public ExternalServiceBadResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}

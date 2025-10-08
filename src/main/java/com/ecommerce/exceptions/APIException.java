package com.ecommerce.exceptions;

public class APIException extends RuntimeException {

    // Used in serialization and deserialization
    private static final long serialVersionUID = 1L;

    public APIException(String message) {
        super(message);

    }
    public APIException() {
    }
}

package com.awesometodo.exception;

public class InvalidCredentialsException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Invalid credentials provided";

    public  InvalidCredentialsException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message,Throwable cause) {
        super(message,cause);
    }


}

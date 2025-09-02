package com.awesometodo.exception;

public class PendingSignupUserWithSameDetailsAlreadyExistsException extends RuntimeException{

    public PendingSignupUserWithSameDetailsAlreadyExistsException() {
        super();
    }

    public PendingSignupUserWithSameDetailsAlreadyExistsException(String message) {
        super(message);
    }

    public PendingSignupUserWithSameDetailsAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.awesometodo.exception;

public class PendingSignupUserDoesntExistException extends RuntimeException{


    public PendingSignupUserDoesntExistException(String message) {
        super(message);
    }
}

package com.awesometodo.exception;

public class ConcurrentOperationException extends RuntimeException{

    public ConcurrentOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

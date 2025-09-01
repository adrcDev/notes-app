package com.awesometodo.exception;

public class UserWithSameDetailsAlreadyExistsException extends RuntimeException{

    public UserWithSameDetailsAlreadyExistsException() {
        super();
    }

    public UserWithSameDetailsAlreadyExistsException(String message) {
        super(message);
    }

    public UserWithSameDetailsAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.awesometodo.exception;

public class SignupOtpsExpiredException extends  RuntimeException{
    public SignupOtpsExpiredException(String message) {
        super(message);
    }
}

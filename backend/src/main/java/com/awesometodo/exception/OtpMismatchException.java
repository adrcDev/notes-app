package com.awesometodo.exception;

public class OtpMismatchException extends RuntimeException{
    private final static String DEFAULT_MESSAGE="Otp mismatch";

    public OtpMismatchException() {
        super(DEFAULT_MESSAGE);
    }

    public OtpMismatchException(String message) {
        super(message);
    }

}

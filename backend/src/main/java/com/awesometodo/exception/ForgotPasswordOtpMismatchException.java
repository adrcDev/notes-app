package com.awesometodo.exception;

import com.awesometodo.entity.ForgotPasswordOtp;

public class ForgotPasswordOtpMismatchException extends  RuntimeException{
    public ForgotPasswordOtpMismatchException(String message) {
        super(message);
    }
}

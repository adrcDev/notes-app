package com.awesometodo.service;

import org.springframework.stereotype.Service;

@Service
public class OtpService {

    public String sendOtpToEmail(String email) {
        String otp=generateOtp();
        /* Code to send the otp to the received email */
        return otp;
    }

    public String sendOtpToPhoneNo(String phoneNo) {
        String otp=generateOtp();
        /* Code to send the otp to the received email */
        return otp;
    }

    private String generateOtp() {
        return "100000";
    }
}

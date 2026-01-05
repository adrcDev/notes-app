package com.awesometodo.dto;

import com.awesometodo.validation.constraint.Username;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SignupOtpVerificationDataDTO {
    @NotBlank
    @Username
    private String username;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6}$",message="The received phone number otp did not match the required pattern")
    private String phoneNumberOtp;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6}$",message="The received email otp did not match the required pattern")
    private String emailOtp;


    public SignupOtpVerificationDataDTO(String username, String phoneNumberOtp, String emailOtp) {
        this.username = username;
        this.phoneNumberOtp = phoneNumberOtp;
        this.emailOtp = emailOtp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumberOtp() {
        return phoneNumberOtp;
    }

    public void setPhoneNumberOtp(String phoneNumberOtp) {
        this.phoneNumberOtp = phoneNumberOtp;
    }

    public String getEmailOtp() {
        return emailOtp;
    }

    public void setEmailOtp(String emailOtp) {
        this.emailOtp = emailOtp;
    }

    @Override
    public String toString() {
        return "SignupOtpVerificationDataDTO{" +
                "username='" + username + '\'' +
                ", phoneNumberOtp='" + phoneNumberOtp + '\'' +
                ", emailOtp='" + emailOtp + '\'' +
                '}';
    }
}

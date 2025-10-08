package com.awesometodo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ForgotPasswordOtpVerificationDataDTO {
    @NotBlank
    private String username;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message ="The received email did not match the required pattern" )
    private String email;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6}$",message="The received email otp did not match the required pattern")
    private String emailOtp;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6}$",message="The received email otp did not match the required pattern")
    private String phoneNumberOtp;

    public ForgotPasswordOtpVerificationDataDTO(String username, String email, String emailOtp, String phoneNumberOtp) {
        this.username = username;
        this.email = email;
        this.emailOtp = emailOtp;
        this.phoneNumberOtp = phoneNumberOtp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailOtp() {
        return emailOtp;
    }

    public void setEmailOtp(String emailOtp) {
        this.emailOtp = emailOtp;
    }

    public String getPhoneNumberOtp() {
        return phoneNumberOtp;
    }

    public void setPhoneNumberOtp(String phoneNumberOtp) {
        this.phoneNumberOtp = phoneNumberOtp;
    }

    @Override
    public String toString() {
        return "ForgotPasswordOtpVerificationDataDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", emailOtp='" + emailOtp + '\'' +
                ", phoneNumberOtp='" + phoneNumberOtp + '\'' +
                '}';
    }
}

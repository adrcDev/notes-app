package com.awesometodo.dto;

import com.awesometodo.validation.constraint.Password;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ForgotPasswordResetDataDTO {
    @NotBlank
    @Pattern(regexp = "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$")
    private String passwordResetToken;


    @NotBlank
    @Password(message = "The received password did not match the required pattern")
    private String newPassword;

    public ForgotPasswordResetDataDTO(String passwordResetToken, String newPassword) {
        this.passwordResetToken = passwordResetToken;
        this.newPassword = newPassword;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "ForgotPasswordResetDataDTO{" +
                "passwordResetToken='" + passwordResetToken + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}

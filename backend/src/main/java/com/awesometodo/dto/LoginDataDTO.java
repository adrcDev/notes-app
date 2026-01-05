package com.awesometodo.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDataDTO {
    @NotBlank
    private String userNameOrEmail;

    @NotBlank
    private String password;

    public LoginDataDTO(String userNameOrEmail, String password) {
        this.userNameOrEmail = userNameOrEmail;
        this.password = password;
    }

    public String getUserNameOrEmail() {
        return userNameOrEmail;
    }

    public void setUserNameOrEmail(String userNameOrEmail) {
        this.userNameOrEmail = userNameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

package com.awesometodo.dto;

import com.awesometodo.validation.constraint.UserNameOrEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

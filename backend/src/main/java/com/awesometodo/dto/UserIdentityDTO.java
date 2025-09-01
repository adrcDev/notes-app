package com.awesometodo.dto;

public class UserIdentityDTO {
    private String username;
    private String email;
    private String phoneNo;

    public UserIdentityDTO(String username, String email, String phoneNo) {
        this.username = username;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public String toString() {
        return "UserIdentityDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                '}';
    }
}

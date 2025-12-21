package com.awesometodo.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class UserDetailsResponseDTO {
    private int id;
    private String username;
    private String displayName;
    private String email;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNo;
    private OffsetDateTime accountCreatedAt;

    public UserDetailsResponseDTO(int id, String username, String displayName, String email, LocalDate dateOfBirth, String gender, String phoneNo, OffsetDateTime accountCreatedAt) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNo = phoneNo;
        this.accountCreatedAt = accountCreatedAt;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public OffsetDateTime getAccountCreatedAt() {
        return accountCreatedAt;
    }

    @Override
    public String toString() {
        return "UserDetailsResponseDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", accountCreatedAt=" + accountCreatedAt +
                '}';
    }
}

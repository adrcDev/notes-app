package com.awesometodo.entity;

import com.awesometodo.converter.GenderEnumToStringConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

//    @Enumerated(EnumType.STRING)
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "gender", nullable = false)
    @Convert(converter = GenderEnumToStringConverter.class)
    private Gender gender;

    @Column(name = "account_created_at", nullable = false,insertable = false,updatable = false)
    private OffsetDateTime accountCreatedAt;

    public enum Gender {
        MALE, FEMALE, OTHER, RATHER_NOT_SAY
    }

    public User() {
    }

    public User(String userName, String displayName, String email, String passwordHash,
                LocalDate dateOfBirth, Gender gender) {
        this.userName = userName;
        this.displayName = displayName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }



    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public OffsetDateTime getAccountCreatedAt() {
        return accountCreatedAt;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + passwordHash + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender=" + gender +
                ", accountCreatedAt=" + accountCreatedAt +
                '}';
    }
}

package com.awesometodo.entity;

import com.awesometodo.entity.converter.GenderEnumToStringConverter;
import com.awesometodo.entity.enums.Gender;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;

@Entity
@Table(name="pending_signup_users")
public class PendingSignupUser {
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

    @Column(name="phone_no",nullable = false,unique = true)
    private String phoneNo;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "gender", nullable = false)
    @Convert(converter = GenderEnumToStringConverter.class)
    private Gender gender;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    public PendingSignupUser() {

    }

    public PendingSignupUser(String userName, String displayName, String email, String phoneNo, LocalDate dateOfBirth, Gender gender, String passwordHash) {
        this.userName = userName;
        this.displayName = displayName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.passwordHash = passwordHash;
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

    public String getPhoneNo() {
        return phoneNo;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public String getPasswordHash() {
        return passwordHash;
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

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "PendingSignupUser{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender=" + gender +
                ", passwordHash='" + passwordHash + '\'' +
                '}';
    }
}

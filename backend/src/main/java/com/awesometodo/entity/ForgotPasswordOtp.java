package com.awesometodo.entity;

import com.awesometodo.entity.converter.OtpTypeToStringConverter;
import com.awesometodo.entity.enums.OtpType;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="forgot_password_otps")
public class ForgotPasswordOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name="otp",nullable = false,length = 6)
    private String otp;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="user_id",referencedColumnName = "id",nullable = false)
    private User user;

    @Convert(converter = OtpTypeToStringConverter.class)
    @Column(name="type",nullable = false)
    private OtpType type;

    @Column(name="expires_at",nullable = false,insertable = false)
    private OffsetDateTime expiresAt;

    public ForgotPasswordOtp() {

    }

    public ForgotPasswordOtp(String otp, User user, OtpType type) {
        this.otp = otp;
        this.user = user;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getOtp() {
        return otp;
    }

    public User getUser() {
        return user;
    }

    public OtpType getType() {
        return type;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setType(OtpType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ForgotPasswordOtp{" +
                "id=" + id +
                ", otp='" + otp + '\'' +
                ", user=" + user +
                ", type=" + type +
                ", expiresAt=" + expiresAt +
                '}';
    }
}

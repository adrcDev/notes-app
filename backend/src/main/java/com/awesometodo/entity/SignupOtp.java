package com.awesometodo.entity;

import com.awesometodo.entity.converter.GenderEnumToStringConverter;
import com.awesometodo.entity.converter.OtpTypeToStringConverter;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="signup_otps")
public class SignupOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name="otp",nullable = false,length = 6)
    String otp;

    public enum OtpType {
        EMAIL,PHONE
    }

    @Column(name="type",nullable = false)
    @Convert(converter = OtpTypeToStringConverter.class)
    private OtpType type;

    @Column(name="expires_at",nullable = false,insertable = false)
    private OffsetDateTime expiresAt;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name="pending_signup_user_id",referencedColumnName = "id",nullable = false)
    private PendingSignupUser pendingSignupUser;

    public SignupOtp() {}

    public SignupOtp(String otp, OtpType type, PendingSignupUser pendingSignupUser) {
        this.otp = otp;
        this.type = type;
        this.pendingSignupUser = pendingSignupUser;
    }

    public int getId() {
        return id;
    }

    public String getOtp() {
        return otp;
    }

    public OtpType getType() {
        return type;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public PendingSignupUser getPendingSignupUser() {
        return pendingSignupUser;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setType(OtpType type) {
        this.type = type;
    }

    public void setPendingSignupUser(PendingSignupUser pendingSignupUser) {
        this.pendingSignupUser = pendingSignupUser;
    }

    @Override
    public String toString() {
        return "SignupOtp{" +
                "id=" + id +
                ", otp='" + otp + '\'' +
                ", type=" + type +
                ", expiresAt=" + expiresAt +
                ", pendingSignupUser=" + pendingSignupUser +
                '}';
    }
}

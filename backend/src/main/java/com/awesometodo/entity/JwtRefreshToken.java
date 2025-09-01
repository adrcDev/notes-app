package com.awesometodo.entity;

import com.awesometodo.entity.converter.StatusEnumToStringConverter;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="jwt_refresh_tokens")
public class JwtRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",referencedColumnName = "id",nullable = false)
    private User userAssociatedWithRefreshToken;

    @Column(name="refresh_token_hash",nullable = false,unique = true)
    private String refreshTokenHash;

    public enum Status {
        VALID, INVALIDATED, COMPROMISED;
    }

    @Column(name="status",nullable = false)
    @Convert(converter = StatusEnumToStringConverter.class)
    private Status status;

    @Column(name="issued_at",nullable = false)
    private OffsetDateTime issuedAt;

    @Column(name="expires_at",nullable = false)
    private OffsetDateTime expiresAt;

    public JwtRefreshToken(User userAssociatedWithRefreshToken, String refreshTokenHash, Status status, OffsetDateTime issuedAt, OffsetDateTime expiresAt) {
        this.userAssociatedWithRefreshToken = userAssociatedWithRefreshToken;
        this.refreshTokenHash = refreshTokenHash;
        this.status = status;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public int getId() {
        return id;
    }

    public User getUserAssociatedWithRefreshToken() {
        return userAssociatedWithRefreshToken;
    }

    public String getRefreshTokenHash() {
        return refreshTokenHash;
    }

    public Status getStatus() {
        return status;
    }

    public OffsetDateTime getIssuedAt() {
        return issuedAt;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setUserAssociatedWithRefreshToken(User userAssociatedWithRefreshToken) {
        this.userAssociatedWithRefreshToken = userAssociatedWithRefreshToken;
    }

    public void setRefreshTokenHash(String refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setIssuedAt(OffsetDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "JwtRefreshToken{" +
                "id=" + id +
                ", userAssociatedWithRefreshToken=" + userAssociatedWithRefreshToken +
                ", refreshTokenHash='" + refreshTokenHash + '\'' +
                ", status=" + status +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}

package com.awesometodo.entity;

import com.awesometodo.entity.converter.StatusEnumToStringConverter;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

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

    @Column(name="jti_claim_value_uuid",nullable = false,unique = true)
    private UUID jtiClaimValueUUID;

    @Column(name="refresh_token_hash",nullable = false,unique = true)
    private String refreshTokenHash;

    public enum Status {
        VALID, INVALIDATED;
    }

    @Column(name="status",nullable = false)
    @Convert(converter = StatusEnumToStringConverter.class)
    private Status status;

    @Column(name="issued_at",nullable = false)
    private OffsetDateTime issuedAt;

    @Column(name="expires_at",nullable = false)
    private OffsetDateTime expiresAt;

    public JwtRefreshToken() {}

    public JwtRefreshToken(User userAssociatedWithRefreshToken, UUID jtiClaimValueUUID, String refreshTokenHash, Status status, OffsetDateTime issuedAt, OffsetDateTime expiresAt) {
        this.userAssociatedWithRefreshToken = userAssociatedWithRefreshToken;
        this.jtiClaimValueUUID = jtiClaimValueUUID;
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

    public UUID getJtiClaimValueUUID() {
        return jtiClaimValueUUID;
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

    public void setJtiClaimValueUUID(UUID jtiClaimValueUUID) {
        this.jtiClaimValueUUID = jtiClaimValueUUID;
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
                ", jtiClaimValueUUID=" + jtiClaimValueUUID +
                ", refreshTokenHash='" + refreshTokenHash + '\'' +
                ", status=" + status +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}

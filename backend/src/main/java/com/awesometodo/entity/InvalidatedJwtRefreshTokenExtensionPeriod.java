package com.awesometodo.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="invalidated_jwt_refresh_token_extension_periods")
public class InvalidatedJwtRefreshTokenExtensionPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="invalidated_jwt_refresh_token_id",referencedColumnName = "id",unique = true,nullable = false)
    private JwtRefreshToken jwtRefreshTokenWithInvalidatedStatus;


    @Column(name="extension_period_ends_at",nullable = false,insertable = false)
    private OffsetDateTime extensionPeriodEndsAt;

    public InvalidatedJwtRefreshTokenExtensionPeriod() {}

    public InvalidatedJwtRefreshTokenExtensionPeriod(JwtRefreshToken jwtRefreshTokenWithInvalidatedStatus) {
        this.jwtRefreshTokenWithInvalidatedStatus = jwtRefreshTokenWithInvalidatedStatus;
    }

    public int getId() {
        return id;
    }

    public JwtRefreshToken getJwtRefreshTokenWithInvalidatedStatus() {
        return jwtRefreshTokenWithInvalidatedStatus;
    }

    public OffsetDateTime getExtensionPeriodEndsAt() {
        return extensionPeriodEndsAt;
    }

    public void setJwtRefreshTokenWithInvalidatedStatus(JwtRefreshToken jwtRefreshTokenWithInvalidatedStatus) {
        this.jwtRefreshTokenWithInvalidatedStatus = jwtRefreshTokenWithInvalidatedStatus;
    }

    @Override
    public String toString() {
        return "InvalidatedJwtRefreshTokenExtensionPeriod{" +
                "id=" + id +
                ", jwtRefreshTokenWithInvalidatedStatus=" + jwtRefreshTokenWithInvalidatedStatus +
                ", extensionPeriodEndsAt=" + extensionPeriodEndsAt +
                '}';
    }
}

package com.awesometodo.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name="password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name="token",nullable = false,insertable = false)
    private UUID token;

    @OneToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",referencedColumnName = "id",unique = true,nullable = false)
    private User user;

    @Column(name="expires_at",nullable = false,insertable = false)
    private OffsetDateTime expiresAt;

    public PasswordResetToken() {}

    public PasswordResetToken(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public UUID getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "id=" + id +
                ", token=" + token +
                ", user=" + user +
                ", expiresAt=" + expiresAt +
                '}';
    }
}

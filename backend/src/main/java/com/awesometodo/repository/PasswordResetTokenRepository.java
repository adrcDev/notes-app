package com.awesometodo.repository;

import com.awesometodo.entity.PasswordResetToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PasswordResetTokenRepository {
    private EntityManager em;

    public PasswordResetTokenRepository(EntityManager em) {
        this.em=em;
    }

    public void delete(PasswordResetToken passwordResetToken) {
        em.remove(passwordResetToken);
        em.flush();
    }

    public Optional<PasswordResetToken> findByUserId(int userId) {
        try {
            PasswordResetToken passwordResetToken = (PasswordResetToken) em.createNativeQuery("SELECT * FROM password_reset_tokens WHERE user_id=:userId", PasswordResetToken.class).setParameter("userId", userId).getSingleResult();
            return Optional.of(passwordResetToken);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }

    public void insertAndRefresh(PasswordResetToken passwordResetToken) {
        em.persist(passwordResetToken);
        em.refresh(passwordResetToken);
    }

    public Optional<PasswordResetToken> findByToken(UUID token) {
        try {
            PasswordResetToken passwordResetToken = (PasswordResetToken) em.createNativeQuery("SELECT * FROM password_reset_tokens WHERE token=:token", PasswordResetToken.class).setParameter("token",token).getSingleResult();
            return Optional.of(passwordResetToken);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Boolean> isExpired(PasswordResetToken passwordResetToken) {
        try {
            boolean result=(Boolean)em.createNativeQuery("SELECT CURRENT_TIMESTAMP>expires_at FROM password_reset_tokens WHERE token=:token", Boolean.class).setParameter("token", passwordResetToken.getToken()).getSingleResult();
            return Optional.of(result);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }


}

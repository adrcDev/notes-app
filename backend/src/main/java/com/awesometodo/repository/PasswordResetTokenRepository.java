package com.awesometodo.repository;

import com.awesometodo.entity.PasswordResetToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
}

package com.awesometodo.repository;

import com.awesometodo.entity.SignupOtp;
import com.awesometodo.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SignupOtpRepository {

    EntityManager em;

    public SignupOtpRepository(EntityManager em) {
        this.em=em;
    }

    public void insert(SignupOtp signupOtp) {
        em.persist(signupOtp);
    }

    public Optional<Boolean> isOtpsForPendingSignupUserIdExpired(int pendingSignupUserId) {
        try {
            Boolean result = (Boolean) em.createNativeQuery("SELECT (CURRENT_TIMESTAMP>expires_at) FROM signup_otps WHERE pending_signup_user_id=:pendingSignupUserId AND type='email';").setParameter("pendingSignupUserId", pendingSignupUserId).getSingleResult();
            return Optional.of(result);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }
}

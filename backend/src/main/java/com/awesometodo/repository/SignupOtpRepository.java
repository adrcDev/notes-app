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

    public Optional<String> findPhoneNumberOtpByPendingSignupUserId(int pendingSignupUserId) {
        try {
            String phoneNumberOtp =(String)em.createNativeQuery("SELECT otp FROM signup_otps WHERE pending_signup_user_id=:pendingSignupUserId AND type='phone'", String.class).setParameter("pendingSignupUserId", pendingSignupUserId).getSingleResult();
            return Optional.of(phoneNumberOtp);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<String> findEmailOtpByPendingSignupUserId(int pendingSignupUserId) {
        try {
            String emailOtp =(String)em.createNativeQuery("SELECT otp FROM signup_otps WHERE pending_signup_user_id=:pendingSignupUserId AND type='email'", String.class).setParameter("pendingSignupUserId", pendingSignupUserId).getSingleResult();
            return Optional.of(emailOtp);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }

    public int updatePhoneNumberOtpByPendingSignupUserId(String newPhoneNumberOtp,int pendingSignupUserId) {
        int noOfRowsUpdated=em.createNativeQuery("UPDATE signup_otps SET otp=:newPhoneNumberOtp,expires_at=CURRENT_TIMESTAMP + INTERVAL '5 minutes' WHERE type='phone' AND pending_signup_user_id=:pendingSignupUserId")
                .setParameter("newPhoneNumberOtp",newPhoneNumberOtp)
                .setParameter("pendingSignupUserId",pendingSignupUserId)
                .executeUpdate();
        return noOfRowsUpdated;
    }

    public int updateEmailOtpByPendingSignupUserId(String newEmailOtp,int pendingSignupUserId) {
        int noOfRowsUpdated=em.createNativeQuery("UPDATE signup_otps SET otp=:newEmailOtp,expires_at=CURRENT_TIMESTAMP + INTERVAL '5 minutes' WHERE type='email' AND pending_signup_user_id=:pendingSignupUserId")
                .setParameter("newEmailOtp",newEmailOtp)
                .setParameter("pendingSignupUserId",pendingSignupUserId)
                .executeUpdate();
        return noOfRowsUpdated;
    }

}

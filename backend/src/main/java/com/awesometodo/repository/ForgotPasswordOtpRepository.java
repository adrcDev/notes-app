package com.awesometodo.repository;

import com.awesometodo.entity.ForgotPasswordOtp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ForgotPasswordOtpRepository {
    private EntityManager em;

    public ForgotPasswordOtpRepository(EntityManager em) {
        this.em=em;
    }

    public List<ForgotPasswordOtp> findByUserId(int userId) {
        List<ForgotPasswordOtp> forgotPasswordOtps=em.createNativeQuery("SELECT * FROM forgot_password_otps WHERE user_id=:userId", ForgotPasswordOtp.class).setParameter("userId",userId).getResultList();
        return forgotPasswordOtps;
    }

    public void delete(ForgotPasswordOtp forgotPasswordOtp) {
        em.remove(forgotPasswordOtp);
        em.flush();
    }

    public void insert(ForgotPasswordOtp forgotPasswordOtp) {
        em.persist(forgotPasswordOtp);
    }
}

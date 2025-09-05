package com.awesometodo.repository;

import com.awesometodo.dto.UserIdentityDTO;
import com.awesometodo.entity.PendingSignupUser;
import com.awesometodo.util.EnumUtil;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PendingSignupUserRepository {
    private EntityManager em;

    public PendingSignupUserRepository(EntityManager em) {
        this.em=em;
    }

    public List<PendingSignupUser> findByUsernameOrEmailOrPhoneNo(UserIdentityDTO userIdentityDTO) {
        String username=userIdentityDTO.getUsername();
        String email=userIdentityDTO.getEmail();
        String phoneNo=userIdentityDTO.getPhoneNo();
        List<PendingSignupUser> pendingSignupUsers=em.createNativeQuery("SELECT * FROM pending_signup_users WHERE user_name=:username OR email=:email OR phone_no=:phoneNo ORDER BY id", PendingSignupUser.class).setParameter("username",username).setParameter("email",email).setParameter("phoneNo",phoneNo).getResultList();
        return pendingSignupUsers;
    }

    public PendingSignupUser insertAndReturn(PendingSignupUser pendingSignupUser) {
        em.persist(pendingSignupUser);
        em.refresh(pendingSignupUser);
        return pendingSignupUser;
    }


    public void delete(PendingSignupUser user) {
        em.remove(user);
        em.flush();
    }












}

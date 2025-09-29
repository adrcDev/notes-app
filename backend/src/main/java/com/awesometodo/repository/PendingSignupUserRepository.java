package com.awesometodo.repository;

import com.awesometodo.dto.SignupDataDTO;
import com.awesometodo.dto.UserIdentityDTO;
import com.awesometodo.entity.PendingSignupUser;
import com.awesometodo.util.EnumUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public Optional<Integer> findIdByUsername(String username) {
        try {
            Integer id=(Integer) em.createNativeQuery("SELECT id FROM pending_signup_users WHERE user_name=:username", Integer.class).setParameter("username", username).getSingleResult();
            return Optional.of(id);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<PendingSignupUser> findById(int id) {
        PendingSignupUser pendingSignupUser=em.find(PendingSignupUser.class,id);
        if(pendingSignupUser==null)
            return Optional.empty();
        return Optional.of(pendingSignupUser);
    }

    public Optional<PendingSignupUser> findBySignUpData(SignupDataDTO signupDataDTO) {
        String usernameLC=signupDataDTO.getUserName().toLowerCase();
        String emailLC=signupDataDTO.getEmail().toLowerCase();
        LocalDate dateOfBirthAsLocalDate=LocalDate.parse(signupDataDTO.getDateOfBirth());
        try {
            PendingSignupUser pendingSignupUser =
                    (PendingSignupUser) em.createNativeQuery("SELECT * FROM pending_signup_users WHERE user_name=:username AND display_name=:displayName AND email=:email AND phone_no=:phoneNo AND date_of_birth=:dateOfBirth AND gender=:gender", PendingSignupUser.class).setParameter("username", usernameLC).setParameter("displayName",signupDataDTO.getUserName()).setParameter("email", emailLC).setParameter("phoneNo", signupDataDTO.getPhoneNumber()).setParameter("dateOfBirth", dateOfBirthAsLocalDate).setParameter("gender", signupDataDTO.getGender()).getSingleResult();
            return Optional.of(pendingSignupUser);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }












}

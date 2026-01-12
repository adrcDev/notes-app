package com.awesometodo.repository;

import com.awesometodo.entity.User;
import com.awesometodo.repository.filter.UserIdentityFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Repository
public class UserRepository {

    EntityManager em;

    public UserRepository(EntityManager em) {
        this.em=em;
    }

     public Optional<User> findByEmail(String email) {
        try {
            User user=(User) em.createNativeQuery("SELECT * FROM users WHERE email=:email", User.class).setParameter("email",email).getSingleResult();
            return Optional.of(user);
        } catch(NoResultException e) {
            return Optional.<User>empty();
        }

    }

    public Optional<User> findByUserName(String userName) {
        try {
            User user=(User) em.createNativeQuery("SELECT * FROM users WHERE user_name=:userName", User.class).setParameter("userName",userName).getSingleResult();
            return Optional.of(user);
        } catch(NoResultException e) {
            return Optional.<User>empty();
        }
    }

    public Optional<User> findById(int id) {
        User user=em.find(User.class,id);
        if(user!=null)
            return Optional.of(user);
        else
            return Optional.<User>empty();

    }

    public boolean isExistsByOringUserIdentityFilter(UserIdentityFilter userIdentityFilter) {
        String username=userIdentityFilter.getUsername().toLowerCase(Locale.ROOT);
        String email=userIdentityFilter.getEmail().toLowerCase(Locale.ROOT);
        String phoneNo=userIdentityFilter.getPhoneNo();

        List<User> users=em.createNativeQuery("SELECT * FROM users WHERE user_name=:username OR email=:email OR phone_no=:phoneNo",User.class).setParameter("username",username).setParameter("email",email).setParameter("phoneNo",phoneNo)
                .getResultList();

        if(users.isEmpty())
            return false;
        else
            return true;
    }


    public void insert(User user) {
        em.persist(user);
    }

    public Optional<User> findByUsernameAndEmail(String username,String email) {
        try {
            User user = (User) em.createNativeQuery("SELECT * FROM users WHERE user_name=:username AND email=:email", User.class).setParameter("username", username).setParameter("email", email).getSingleResult();
            return Optional.of(user);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }

    public boolean isExistsById(int userId) {
        User user=em.find(User.class,userId);
        if(user==null)
            return false;
        return true;
    }






}

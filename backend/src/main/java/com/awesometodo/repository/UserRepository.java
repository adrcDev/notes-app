package com.awesometodo.repository;

import com.awesometodo.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

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







}

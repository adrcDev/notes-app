package com.awesometodo.repository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordResetTokenRepository {
    private EntityManager em;

    public PasswordResetTokenRepository(EntityManager em) {
        this.em=em;
    }
}

package com.awesometodo.repository;

import com.awesometodo.entity.JwtRefreshToken;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class JwtRefreshTokenRepository {

    private EntityManager em;

    public JwtRefreshTokenRepository(EntityManager em) {
        this.em=em;
    }

    public void insertJwtRefreshToken(JwtRefreshToken jwtRefreshToken) {
        em.persist(jwtRefreshToken);
    }
}

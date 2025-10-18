package com.awesometodo.repository;

import com.awesometodo.entity.InvalidatedJwtRefreshTokenExtensionPeriod;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InvalidatedJwtRefreshTokenExtensionPeriodRepository {
    private EntityManager em;

    public InvalidatedJwtRefreshTokenExtensionPeriodRepository(EntityManager em) {
        this.em=em;
    }

    public void insert(InvalidatedJwtRefreshTokenExtensionPeriod invalidatedJwtRefreshTokenExtensionPeriod) {
        em.persist(invalidatedJwtRefreshTokenExtensionPeriod);
    }

    public Optional<Boolean> isExtensionPeriodExpiredForInvalidatedJwtRefreshTokenId(int invalidatedJwtRefreshTokenId) {
        try {
            boolean result = (Boolean) em.createNativeQuery("SELECT CURRENT_TIMESTAMP>extension_period_ends_at FROM invalidated_jwt_refresh_token_extension_periods WHERE invalidated_jwt_refresh_token_id=:invalidatedJwtRefreshTokenId", Boolean.class).setParameter("invalidatedJwtRefreshTokenId", invalidatedJwtRefreshTokenId).getSingleResult();
            return Optional.of(result);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }
}

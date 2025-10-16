package com.awesometodo.repository;

import com.awesometodo.entity.JwtRefreshToken;
import com.awesometodo.util.EnumUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JwtRefreshTokenRepository {

    private EntityManager em;

    public JwtRefreshTokenRepository(EntityManager em) {
        this.em=em;
    }

    public void insert(JwtRefreshToken jwtRefreshToken) {
        em.persist(jwtRefreshToken);
    }

    public int updateAllValidStatusToInvalidatedForUserId(int userId) {
        String invalidatedStatusEnumAsString=EnumUtil.convertToSpaceSeparatedLowerCaseString(JwtRefreshToken.Status.INVALIDATED).get();
        String validStatusEnumAsString=EnumUtil.convertToSpaceSeparatedLowerCaseString(JwtRefreshToken.Status.VALID).get();
        int updatedRowsCount=
                em.createNativeQuery("UPDATE jwt_refresh_tokens SET status=:invalidatedStatus WHERE user_id=:userId AND status=:validStatus")
                .setParameter("userId",userId)
                .setParameter("invalidatedStatus",invalidatedStatusEnumAsString)
                .setParameter("validStatus",validStatusEnumAsString ).executeUpdate();
        return updatedRowsCount;
    }

    public Optional<JwtRefreshToken> findByJtiClaimValueUUIDAndUserId(UUID jtiClaimValueUUID,int userId,boolean isResultRowToBeLocked) {
        String query="SELECT * FROM jwt_refresh_tokens WHERE jti_claim_value_uuid=:jtiClaimValueUUID AND user_id=:userId";
        if(isResultRowToBeLocked) {
            query=query+" FOR UPDATE";
        }
        try {
            JwtRefreshToken jwtRefreshToken=(JwtRefreshToken) em.createNativeQuery(query, JwtRefreshToken.class).setParameter("jtiClaimValueUUID", jtiClaimValueUUID).setParameter("userId",userId).getSingleResult();
            return Optional.of(jwtRefreshToken);
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }

    public int updateStatusOfAllJwtRefreshTokensForUserId(int userId,JwtRefreshToken.Status status) {
        String statusAsString=EnumUtil.convertToSpaceSeparatedLowerCaseString(status).get();
        int noOfUpdatedRows=em.createNativeQuery("UPDATE jwt_refresh_tokens SET status=:status WHERE user_id=:userId").setParameter("status",statusAsString).setParameter("userId",userId).executeUpdate();
        return noOfUpdatedRows;
    }
}

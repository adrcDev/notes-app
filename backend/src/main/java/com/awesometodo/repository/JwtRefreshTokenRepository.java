package com.awesometodo.repository;

import com.awesometodo.entity.JwtRefreshToken;
import com.awesometodo.util.EnumUtil;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JwtRefreshTokenRepository {

    private EntityManager em;

    public JwtRefreshTokenRepository(EntityManager em) {
        this.em=em;
    }

    public void insertJwtRefreshToken(JwtRefreshToken jwtRefreshToken) {
        em.persist(jwtRefreshToken);
    }

//    public List<JwtRefreshToken> findAllByUserIdAndStatus(int userId, JwtRefreshToken.Status status) {
//        List<JwtRefreshToken> jwtRefreshTokenList=(List<JwtRefreshToken>)em.createNativeQuery("SELECT * FROM jwt_refresh_tokens WHERE user_id=:userId AND status=:status",JwtRefreshToken.class).setParameter("userId",userId).setParameter("status", status).getResultList();
//        return jwtRefreshTokenList;
//    }

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
}

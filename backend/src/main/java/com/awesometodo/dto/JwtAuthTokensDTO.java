package com.awesometodo.dto;

public class JwtAuthTokensDTO {
    private String jwtAccessToken;
    private String jwtRefreshToken;

    public JwtAuthTokensDTO(String jwtAccessToken, String jwtRefreshToken) {
        this.jwtAccessToken = jwtAccessToken;
        this.jwtRefreshToken = jwtRefreshToken;
    }

    public String getJwtAccessToken() {
        return jwtAccessToken;
    }

    public void setJwtAccessToken(String jwtAccessToken) {
        this.jwtAccessToken = jwtAccessToken;
    }

    public String getJwtRefreshToken() {
        return jwtRefreshToken;
    }

    public void setJwtRefreshToken(String jwtRefreshToken) {
        this.jwtRefreshToken = jwtRefreshToken;
    }

    @Override
    public String toString() {
        return "JwtAuthTokensDTO{" +
                "jwtAccessToken='" + jwtAccessToken + '\'' +
                ", jwtRefreshToken='" + jwtRefreshToken + '\'' +
                '}';
    }
}

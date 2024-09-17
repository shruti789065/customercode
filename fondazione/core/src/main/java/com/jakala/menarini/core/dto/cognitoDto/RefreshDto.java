package com.jakala.menarini.core.dto.cognitoDto;

public class RefreshDto {

    private String refreshToken;
    private String token;
    private String email;

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

package com.jakala.menarini.core.dto.cognitoDto;

public class RefreshDto {

    private String refreshToken;
    private String email;

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

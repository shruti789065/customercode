package com.jakala.menarini.core.dto.cognito;

public class CognitoAuthResultDto {

    private String AccessToken;
    private String IdToken;
    private int ExpiresIn;
    private String RefreshToken;
    private String TokenType;

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    public String getIdToken() {
        return IdToken;
    }

    public void setIdToken(String idToken) {
        IdToken = idToken;
    }

    public int getExpiresIn() {
        return ExpiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        ExpiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return RefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        RefreshToken = refreshToken;
    }

    public String getTokenType() {
        return TokenType;
    }

    public void setTokenType(String tokenType) {
        TokenType = tokenType;
    }
}

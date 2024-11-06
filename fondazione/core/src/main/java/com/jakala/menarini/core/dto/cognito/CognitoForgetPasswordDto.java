package com.jakala.menarini.core.dto.cognito;

public class CognitoForgetPasswordDto {

    private String Username;
    private String ClientId;
    private String SecretHash;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public String getSecretHash() {
        return SecretHash;
    }

    public void setSecretHash(String secretHash) {
        SecretHash = secretHash;
    }
}

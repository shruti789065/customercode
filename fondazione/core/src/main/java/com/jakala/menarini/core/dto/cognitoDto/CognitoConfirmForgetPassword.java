package com.jakala.menarini.core.dto.cognitoDto;

public class CognitoConfirmForgetPassword {

    private String Username;
    private String ClientId;
    private String SecretHash;
    private String Password;
    private String ConfirmationCode;

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

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getConfirmationCode() {
        return ConfirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        ConfirmationCode = confirmationCode;
    }
}

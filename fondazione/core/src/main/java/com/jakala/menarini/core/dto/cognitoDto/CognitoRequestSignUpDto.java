package com.jakala.menarini.core.dto.cognitoDto;

import java.util.ArrayList;

public class CognitoRequestSignUpDto {

    private String ClientId;
    private String Username;
    private String Password;
    private String SecretHash;
    private ArrayList<CognitoRequestUserAttributeDto> UserAttributes;


    public CognitoRequestSignUpDto() {
        super();
        this.UserAttributes = new ArrayList<>();
    }


    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getSecretHash() {
        return SecretHash;
    }

    public void setSecretHash(String secretHash) {
        SecretHash = secretHash;
    }

    public ArrayList<CognitoRequestUserAttributeDto> getUserAttributes() {
        return UserAttributes;
    }

    public void setUserAttributes(ArrayList<CognitoRequestUserAttributeDto> userAttributes) {
        UserAttributes = userAttributes;
    }
}

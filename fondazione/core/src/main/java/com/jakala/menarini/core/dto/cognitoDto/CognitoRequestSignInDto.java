package com.jakala.menarini.core.dto.cognitoDto;

public class CognitoRequestSignInDto {

    private final String AuthFlow = "USER_PASSWORD_AUTH";
    private String ClientId;
    private CognitoAuthParametersDto AuthParameters;


    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public CognitoAuthParametersDto getAuthParameters() {
        return AuthParameters;
    }

    public void setAuthParameters(CognitoAuthParametersDto authParameters) {
        AuthParameters = authParameters;
    }
}

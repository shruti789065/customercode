package com.jakala.menarini.core.dto.cognitoDto;

public class CognitoRequestRefreshDto {

    private final String AuthFlow = "REFRESH_TOKEN";
    private String ClientId;
    private CognitoRefreshAuthParamDto AuthParameters;


    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public CognitoRefreshAuthParamDto getAuthParameters() {
        return AuthParameters;
    }

    public void setAuthParameters(CognitoRefreshAuthParamDto authParameters) {
        AuthParameters = authParameters;
    }

}

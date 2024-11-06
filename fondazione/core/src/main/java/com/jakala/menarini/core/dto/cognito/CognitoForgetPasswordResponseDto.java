package com.jakala.menarini.core.dto.cognito;

@SuppressWarnings("squid:S2384")
public class CognitoForgetPasswordResponseDto {

    private CognitoForgetPasswordDestinationDto CodeDeliveryDetails;

    public CognitoForgetPasswordDestinationDto getCodeDeliveryDetails() {
        return CodeDeliveryDetails;
    }

    public void setCodeDeliveryDetails(CognitoForgetPasswordDestinationDto codeDeliveryDetails) {
        CodeDeliveryDetails = codeDeliveryDetails;
    }
}

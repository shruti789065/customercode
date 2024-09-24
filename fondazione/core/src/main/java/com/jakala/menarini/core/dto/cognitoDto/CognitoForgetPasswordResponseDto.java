package com.jakala.menarini.core.dto.cognitoDto;

public class CognitoForgetPasswordResponseDto {

    private CognitoForgetPasswordDestinationDto CodeDeliveryDetails;

    public CognitoForgetPasswordDestinationDto getCodeDeliveryDetails() {
        return CodeDeliveryDetails;
    }

    public void setCodeDeliveryDetails(CognitoForgetPasswordDestinationDto codeDeliveryDetails) {
        CodeDeliveryDetails = codeDeliveryDetails;
    }
}

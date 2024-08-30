package com.jakala.menarini.core.dto.cognitoDto;

public class SignInResponseDto {


    private CognitoSignInErrorResponseDto cognitoSignInErrorResponseDto;


    private CognitoAuthResultDto AuthenticationResult;

    public CognitoAuthResultDto getCognitoAuthResultDto() {
        return AuthenticationResult;
    }

    public void setCognitoAuthResultDto(CognitoAuthResultDto AuthenticationResult) {
        this.AuthenticationResult = AuthenticationResult;
    }

    public CognitoSignInErrorResponseDto getCognitoSignInErrorResponseDto() {
        return cognitoSignInErrorResponseDto;
    }

    public void setCognitoSignInErrorResponseDto(CognitoSignInErrorResponseDto cognitoSignInErrorResponseDto) {
        this.cognitoSignInErrorResponseDto = cognitoSignInErrorResponseDto;
    }
}

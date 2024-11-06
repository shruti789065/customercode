package com.jakala.menarini.core.dto.cognito;

@SuppressWarnings("squid:S2384")
public class ForgetPasswordResponseDto {

    private boolean success;
    private CognitoForgetPasswordResponseDto cognitoForgetPasswordResponseDto;
    private CognitoSignInErrorResponseDto cognitoError;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CognitoForgetPasswordResponseDto getCognitoForgetPasswordResponseDto() {
        return cognitoForgetPasswordResponseDto;
    }

    public void setCognitoForgetPasswordResponseDto(CognitoForgetPasswordResponseDto cognitoForgetPasswordResponseDto) {
        this.cognitoForgetPasswordResponseDto = cognitoForgetPasswordResponseDto;
    }

    public CognitoSignInErrorResponseDto getCognitoError() {
        return cognitoError;
    }

    public void setCognitoError(CognitoSignInErrorResponseDto cognitoError) {
        this.cognitoError = cognitoError;
    }
}

package com.jakala.menarini.core.dto.cognitoDto;

public class ConfirmForgetPasswordResponseDto {
    private boolean success;
    private CognitoSignInErrorResponseDto cognitoError;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CognitoSignInErrorResponseDto getCognitoError() {
        return cognitoError;
    }

    public void setCognitoError(CognitoSignInErrorResponseDto cognitoError) {
        this.cognitoError = cognitoError;
    }
}

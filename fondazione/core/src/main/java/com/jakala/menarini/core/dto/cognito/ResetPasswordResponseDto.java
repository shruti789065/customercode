package com.jakala.menarini.core.dto.cognito;

@SuppressWarnings("squid:S2384")
public class ResetPasswordResponseDto {

    private boolean success;
    private CognitoSignInErrorResponseDto error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CognitoSignInErrorResponseDto getError() {
        return error;
    }

    public void setError(CognitoSignInErrorResponseDto error) {
        this.error = error;
    }
}

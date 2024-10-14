package com.jakala.menarini.core.dto;

public class RegisteredUseServletResponseDto {


    private boolean success;
    private RegisteredUserDto updatedUser;
    private String errorMessage;
    private Boolean islogOut;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public RegisteredUserDto getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(RegisteredUserDto updatedUser) {
        this.updatedUser = updatedUser;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getIslogOut() {
        return islogOut;
    }

    public void setIslogOut(Boolean islogOut) {
        this.islogOut = islogOut;
    }
}

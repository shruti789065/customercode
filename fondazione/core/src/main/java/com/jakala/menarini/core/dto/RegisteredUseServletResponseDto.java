package com.jakala.menarini.core.dto;

@SuppressWarnings("squid:S2384")
public class RegisteredUseServletResponseDto {

    private boolean success;
    private RegisteredUserDto updatedUser;
    private RegisteredUserPermissionDto userPermission;
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

    public RegisteredUserPermissionDto getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(RegisteredUserPermissionDto userPermission) {
        this.userPermission = userPermission;
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

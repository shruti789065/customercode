package com.jakala.menarini.core.service.interfaces;

import com.jakala.menarini.core.dto.cognito.*;

public interface AwsCognitoServiceInterface {


    public SignInResponseDto loginOnCognito(SignInDto signInDto);
    public SignInResponseDto refreshOnCognito(RefreshDto requestRefreshDto);
    public SignUpDtoResponse registerOnCognito(SignUpDto registrationData);
    public ForgetPasswordResponseDto forgetPassword(ForgetPasswordDto forgetPasswordDto);
    public ConfirmForgetPasswordResponseDto confirmForgetPassword(ConfirmForgetPasswordDto forgetPassword);
    public ResetPasswordResponseDto resetPassword(ResetPasswordDto resetPasswordDto);

}

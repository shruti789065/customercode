package com.jakala.menarini.core.service.interfaces;

import com.jakala.menarini.core.dto.cognitoDto.*;

public interface AwsCognitoServiceInterface {


    public SignInResponseDto loginOnCognito(SignInDto signInDto);
    public SignInResponseDto refreshOnCognito(RefreshDto requestRefreshDto);
    public SignUpDtoResponse registerOnCognito(SignUpDto registrationData);

}

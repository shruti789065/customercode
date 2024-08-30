package com.jakala.menarini.core.service.interfaces;

import com.jakala.menarini.core.dto.cognitoDto.SignInDto;
import com.jakala.menarini.core.dto.cognitoDto.SignInResponseDto;
import com.jakala.menarini.core.dto.cognitoDto.SignUpDto;
import com.jakala.menarini.core.dto.cognitoDto.SignUpDtoResponse;

public interface AwsCognitoServiceInterface {


    public SignInResponseDto loginOnCognito(SignInDto signInDto);
    public SignUpDtoResponse registerOnCognito(SignUpDto registrationData);

}

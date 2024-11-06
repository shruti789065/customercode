package com.jakala.menarini.core.models;

import com.jakala.menarini.core.dto.cognito.SignUpDto;
import com.jakala.menarini.core.dto.cognito.SignUpDtoResponse;
import com.jakala.menarini.core.service.AwsCognitoService;
import com.jakala.menarini.core.service.UserRegisteredService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = Resource.class)
public class SignUp extends SignUpDto {

    @OSGiService
    private AwsCognitoService awsCognitoService;

    private boolean err;

    private String errorMessage;

    @OSGiService
    private UserRegisteredService userRegisteredService;

    public void signUp() {
        SignUpDtoResponse response =  this.awsCognitoService.registerOnCognito(this);
        if(response.getCognitoSignUpErrorResponseDto() != null) {
            this.err = true;
            this.errorMessage = response.getCognitoSignUpErrorResponseDto().getMessage();
        }
        this.err = false;
    }

    public boolean isErr() {
        return err;
    }

    public void setErr(boolean err) {
        this.err = err;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

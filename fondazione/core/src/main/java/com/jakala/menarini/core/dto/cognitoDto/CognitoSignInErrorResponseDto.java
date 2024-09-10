package com.jakala.menarini.core.dto.cognitoDto;

public class CognitoSignInErrorResponseDto {

    private String __type;
    private String message;

    public String get__type() {
        return __type;
    }

    public void set__type(String __type) {
        this.__type = __type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

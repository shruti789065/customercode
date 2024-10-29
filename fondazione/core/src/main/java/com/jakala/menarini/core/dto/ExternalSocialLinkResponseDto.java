package com.jakala.menarini.core.dto;



public class ExternalSocialLinkResponseDto {

    private String type;
    private String redirect;


    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

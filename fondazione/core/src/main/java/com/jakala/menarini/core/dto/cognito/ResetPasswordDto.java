package com.jakala.menarini.core.dto.cognito;

public class ResetPasswordDto {

    private String AccessToken;
    private String PreviousPassword;
    private String ProposedPassword;

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    public String getPreviousPassword() {
        return PreviousPassword;
    }

    public void setPreviousPassword(String previousPassword) {
        PreviousPassword = previousPassword;
    }

    public String getProposedPassword() {
        return ProposedPassword;
    }

    public void setProposedPassword(String proposedPassword) {
        ProposedPassword = proposedPassword;
    }
}

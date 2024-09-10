package com.jakala.menarini.core.dto.cognitoDto;

public class CognitoAuthParametersDto {
    private String USERNAME;
    private String PASSWORD;
    private String SECRET_HASH;

    public String getUSERNAME() {
        return USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public String getSECRET_HASH() {
        return SECRET_HASH;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public void setSECRET_HASH(String SECRET_HASH) {
        this.SECRET_HASH = SECRET_HASH;
    }
}

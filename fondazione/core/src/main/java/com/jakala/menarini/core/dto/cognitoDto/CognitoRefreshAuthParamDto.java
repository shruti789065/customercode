package com.jakala.menarini.core.dto.cognitoDto;

public class CognitoRefreshAuthParamDto {

    private String REFRESH_TOKEN;
    private String SECRET_HASH;

    public String getREFRESH_TOKEN() {
        return REFRESH_TOKEN;
    }

    public void setREFRESH_TOKEN(String REFRESH_TOKEN) {
        this.REFRESH_TOKEN = REFRESH_TOKEN;
    }

    public String getSECRET_HASH() {
        return SECRET_HASH;
    }

    public void setSECRET_HASH(String SECRET_HASH) {
        this.SECRET_HASH = SECRET_HASH;
    }
}

package com.jakala.menarini.core.dto;

import com.jakala.menarini.core.dto.enums.ExternalizeOp;

public class ExternalizeLinksDto {

    private ExternalizeOp op;
    private String prevLink;
    private String targetLink;
    private Boolean isAbsolute;


    public ExternalizeOp getOp() {
        return op;
    }

    public void setOp(ExternalizeOp op) {
        this.op = op;
    }

    public String getPrevLink() {
        return prevLink;
    }

    public void setPrevLink(String prevLink) {
        this.prevLink = prevLink;
    }

    public String getTargetLink() {
        return targetLink;
    }

    public void setTargetLink(String targetLink) {
        this.targetLink = targetLink;
    }

    public Boolean getAbsolute() {
        return isAbsolute;
    }

    public void setAbsolute(Boolean absolute) {
        isAbsolute = absolute;
    }
}

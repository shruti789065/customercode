package com.jakala.menarini.core.dto.enums;

import com.google.gson.annotations.SerializedName;

public enum ExternalizeOp {
    @SerializedName("social")
    SOCIAL,
    @SerializedName("request")
    REQUEST,
    @SerializedName("redirect")
    REDIRECT,
    @SerializedName("external")
    EXT_URL
}

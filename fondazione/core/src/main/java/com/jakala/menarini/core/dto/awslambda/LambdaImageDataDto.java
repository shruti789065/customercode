package com.jakala.menarini.core.dto.awslambda;

@SuppressWarnings("squid:S2384")
public class LambdaImageDataDto {

    private String type;
    private byte[] data;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}

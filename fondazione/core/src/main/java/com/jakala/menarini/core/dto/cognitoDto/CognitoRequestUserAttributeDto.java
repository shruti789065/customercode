package com.jakala.menarini.core.dto.cognitoDto;

public class CognitoRequestUserAttributeDto {

    private String Name;
    private String Value;


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}

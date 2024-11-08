package com.jakala.menarini.core.dto.cognito;

public class CognitoForgetPasswordDestinationDto {

    private String AttributeName;
    private String DeliveryMedium;
    private String Destination;

    public String getAttributeName() {
        return AttributeName;
    }

    public void setAttributeName(String attributeName) {
        AttributeName = attributeName;
    }

    public String getDeliveryMedium() {
        return DeliveryMedium;
    }

    public void setDeliveryMedium(String deliveryMedium) {
        DeliveryMedium = deliveryMedium;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }
}

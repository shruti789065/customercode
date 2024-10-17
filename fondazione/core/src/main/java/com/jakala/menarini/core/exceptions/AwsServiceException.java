package com.jakala.menarini.core.exceptions;

public class AwsServiceException extends RuntimeException {
    public AwsServiceException(String message) {
        super(message);
    }
}

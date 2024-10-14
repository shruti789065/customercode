package com.jakala.menarini.core.exceptions;

public class JwtServiceException extends RuntimeException {
    public JwtServiceException(String message) {
        super(message);
    }
}

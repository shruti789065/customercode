package com.jakala.menarini.core.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JwtServiceExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Test message";
        JwtServiceException exception = new JwtServiceException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
}
package com.jakala.menarini.core.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AwsServiceExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Test message";
        AwsServiceException exception = new AwsServiceException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

}
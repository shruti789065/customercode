package com.jakala.menarini.core.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RowProcessExceptionTest {

    @Test
    void testDefaultConstructor() {
        RowProcessException exception = new RowProcessException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessage() {
        String message = "Test message";
        RowProcessException exception = new RowProcessException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Test message";
        Throwable cause = new Throwable("Test cause");
        RowProcessException exception = new RowProcessException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new Throwable("Test cause");
        RowProcessException exception = new RowProcessException(cause);
        assertEquals(cause, exception.getCause());
        assertEquals("java.lang.Throwable: Test cause", exception.getMessage());
    }
}
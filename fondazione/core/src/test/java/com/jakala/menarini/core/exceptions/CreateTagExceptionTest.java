package com.jakala.menarini.core.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CreateTagExceptionTest {

    @Test
    void testDefaultConstructor() {
        CreateTagException exception = new CreateTagException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessage() {
        String message = "Test message";
        CreateTagException exception = new CreateTagException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Test message";
        Throwable cause = new Throwable("Test cause");
        CreateTagException exception = new CreateTagException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new Throwable("Test cause");
        CreateTagException exception = new CreateTagException(cause);
        assertEquals(cause, exception.getCause());
        assertEquals("java.lang.Throwable: Test cause", exception.getMessage());
    }
}
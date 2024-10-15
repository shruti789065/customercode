package com.jakala.menarini.core.exceptions;

public class CreateTagException extends Exception {
    public CreateTagException() {
        super();
    }

    public CreateTagException(String message) {
        super(message);
    }

    public CreateTagException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateTagException(Throwable cause) {
        super(cause);
    }
}
package com.jakala.menarini.core.exceptions;

public class RowProcessException extends Exception {
    
    public RowProcessException() {
        super();
    }

    public RowProcessException(String message) {
        super(message);
    }

    public RowProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public RowProcessException(Throwable cause) {
        super(cause);
    }
}

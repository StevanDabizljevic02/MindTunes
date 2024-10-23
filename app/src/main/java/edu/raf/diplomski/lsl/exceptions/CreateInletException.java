package edu.raf.diplomski.lsl.exceptions;

public class CreateInletException extends RuntimeException {

    // Default constructor
    public CreateInletException() {
        super();
    }

    // Constructor that accepts a message
    public CreateInletException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public CreateInletException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public CreateInletException(Throwable cause) {
        super(cause);
    }

    // Constructor that allows suppression and stack trace writability
    protected CreateInletException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

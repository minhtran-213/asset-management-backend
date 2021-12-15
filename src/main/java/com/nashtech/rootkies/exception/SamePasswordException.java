package com.nashtech.rootkies.exception;

public class SamePasswordException extends RuntimeException{
    public SamePasswordException() {
        super();
    }

    public SamePasswordException(String message) {
        super(message);
    }

    public SamePasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public SamePasswordException(Throwable cause) {
        super(cause);
    }
}

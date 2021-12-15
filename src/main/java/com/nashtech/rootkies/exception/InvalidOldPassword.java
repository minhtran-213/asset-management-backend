package com.nashtech.rootkies.exception;

public class InvalidOldPassword extends RuntimeException{
    public InvalidOldPassword() {
        super();
    }

    public InvalidOldPassword(String message) {
        super(message);
    }

    public InvalidOldPassword(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOldPassword(Throwable cause) {
        super(cause);
    }
}

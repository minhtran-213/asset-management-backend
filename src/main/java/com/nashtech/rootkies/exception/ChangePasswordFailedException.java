package com.nashtech.rootkies.exception;

public class ChangePasswordFailedException extends Exception{
    public ChangePasswordFailedException() {
    }

    public ChangePasswordFailedException(String message) {
        super(message);
    }

    public ChangePasswordFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChangePasswordFailedException(Throwable cause) {
        super(cause);
    }
}

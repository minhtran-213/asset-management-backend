package com.nashtech.rootkies.exception;

public class WrongPasswordOrUsernameException extends RuntimeException{
    public WrongPasswordOrUsernameException() {
        super();
    }

    public WrongPasswordOrUsernameException(String message) {
        super(message);
    }

    public WrongPasswordOrUsernameException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongPasswordOrUsernameException(Throwable cause) {
        super(cause);
    }
}

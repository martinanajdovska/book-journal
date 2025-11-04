package com.bjournal.bookjournal.model.exceptions;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException() {
        super("This can only be accessed by authenticated users");
    }
}

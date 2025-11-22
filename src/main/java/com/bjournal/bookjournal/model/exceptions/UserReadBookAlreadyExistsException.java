package com.bjournal.bookjournal.model.exceptions;

public class UserReadBookAlreadyExistsException extends RuntimeException {
    public UserReadBookAlreadyExistsException() {
        super("User has already read this book");
    }
}

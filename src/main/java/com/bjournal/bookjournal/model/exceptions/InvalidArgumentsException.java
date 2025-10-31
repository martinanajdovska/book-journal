package com.bjournal.bookjournal.model.exceptions;

public class InvalidArgumentsException extends RuntimeException {
    public InvalidArgumentsException() {
        super("Invalid username or password");
    }
}

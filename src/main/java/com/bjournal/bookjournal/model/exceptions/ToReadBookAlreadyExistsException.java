package com.bjournal.bookjournal.model.exceptions;

public class ToReadBookAlreadyExistsException extends RuntimeException {
    public ToReadBookAlreadyExistsException() {
        super("To-read book already exists");
    }
}

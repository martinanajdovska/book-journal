package com.bjournal.bookjournal.model.exceptions;

public class ToReadBookNotFoundException extends RuntimeException {
    public ToReadBookNotFoundException() {
        super("Book not found");
    }
}

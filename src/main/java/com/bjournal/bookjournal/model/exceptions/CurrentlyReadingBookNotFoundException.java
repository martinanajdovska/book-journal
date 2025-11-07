package com.bjournal.bookjournal.model.exceptions;

public class CurrentlyReadingBookNotFoundException extends RuntimeException {
    public CurrentlyReadingBookNotFoundException() {
        super("Book not found");
    }
}

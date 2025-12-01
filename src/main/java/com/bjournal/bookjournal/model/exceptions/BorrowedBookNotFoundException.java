package com.bjournal.bookjournal.model.exceptions;

public class BorrowedBookNotFoundException extends RuntimeException {
    public BorrowedBookNotFoundException() {
        super("Borrowed book not found");
    }
}

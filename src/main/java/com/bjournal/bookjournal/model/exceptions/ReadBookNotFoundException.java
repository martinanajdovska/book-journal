package com.bjournal.bookjournal.model.exceptions;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.User;

public class ReadBookNotFoundException extends RuntimeException {
    public ReadBookNotFoundException(User user, Book book) {
        super(String.format("Book %d not read by user %s", book.getId(),user.getUsername()));
    }
}

package com.bjournal.bookjournal.model.exceptions;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.User;

public class UserReadBookNotFoundException extends RuntimeException {
    public UserReadBookNotFoundException(User user, Book book) {
        super(String.format("Book %d not read by user %s", book.getId(),user.getUsername()));
    }
}

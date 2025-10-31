package com.bjournal.bookjournal.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such book")  // 404
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) { super(String.format("Book %d not found", id)); }
}

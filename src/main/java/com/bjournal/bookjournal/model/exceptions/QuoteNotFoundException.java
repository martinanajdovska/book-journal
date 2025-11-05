package com.bjournal.bookjournal.model.exceptions;

public class QuoteNotFoundException extends RuntimeException {
    public QuoteNotFoundException(Long id) {
        super(String.format("Quote not found for id: %d", id));
    }
}

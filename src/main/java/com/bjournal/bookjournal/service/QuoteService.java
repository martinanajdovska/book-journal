package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.Quote;

import java.util.Optional;

public interface QuoteService {
    void add(String username, Long bookId, String text);
    void deleteById(Long id);
    Optional<Quote> update(Long id, String text);
}

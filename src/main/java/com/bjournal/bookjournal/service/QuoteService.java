package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.Quote;

import java.util.List;
import java.util.Optional;

public interface QuoteService {
    void add(String username, Long bookId, String text);
    void deleteById(Long id);
    Optional<Quote> update(Long id, String text);
    List<Quote> findAllByUsernameAndBookId(String username, Long bookId);
}

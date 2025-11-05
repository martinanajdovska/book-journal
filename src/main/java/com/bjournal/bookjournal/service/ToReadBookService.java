package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.ToReadBook;

import java.util.List;
import java.util.Optional;

public interface ToReadBookService {
    Optional<ToReadBook> findById(Long id);
    void toggleToRead(String username, Long bookId);
    List<ToReadBook> findAllByUsername(String username);
    Optional<ToReadBook> findByUsernameAndBookId(String username, Long bookId);
    List<ToReadBook> findAllByUserAndBookTitleContainingIgnoreCase(String username, String title);
}

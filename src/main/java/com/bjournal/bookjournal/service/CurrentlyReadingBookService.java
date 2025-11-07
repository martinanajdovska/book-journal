package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.CurrentlyReadingBook;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CurrentlyReadingBookService {
    void add(String username, Long bookid);
    void delete(String username, Long bookid);
    List<CurrentlyReadingBook> findAllByUsername(String username);
    List<CurrentlyReadingBook> findAllByUsernameAndBookTitleContainingIgnoreCase(String username, String title);
    Optional<CurrentlyReadingBook> findByUsernameAndBookId(String username, Long bookId);
}

package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.CurrentlyReadingBook;

import java.time.LocalDate;
import java.util.List;

public interface CurrentlyReadingBookService {
    void add(String username, Long bookid, LocalDate startedDate, Float progress);
    List<CurrentlyReadingBook> findAllByUsername(String username);
    List<CurrentlyReadingBook> findAllByUsernameAndBookTitleContainingIgnoreCase(String username, String title);
    void toggleCurrentlyReading(String username, Long bookId);
}

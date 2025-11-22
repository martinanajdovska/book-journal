package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.UserReadBook;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface UserReadBookService {
    List<UserReadBook> findAllByUsername(String username);
    Optional<UserReadBook> findLastByUserUsernameAndBookId(String username, Long bookId);
    void add(LocalDate addedDate, String username, Long bookId);
    void delete(String username, Long bookId);
    void updateDates(String username, Long bookId, LocalDate startedDate, LocalDate finishedDate);
    List<UserReadBook> findAllByUserAndBookTitleContainingIgnoreCase(String username, String title);
}

package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.UserReadBook;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface UserReadBookService {
    List<UserReadBook> findAllByUser(String username);
    Optional<UserReadBook> findByUserAndBook(String username, Book book);
    void add(LocalDate addedDate, String username, Book book);
    void deleteByUserAndBook(String username, Book book);
    void updateDates(UserReadBook userReadBook, LocalDate startedDate, LocalDate finishedDate);
    List<UserReadBook> findAllByUserAndBookTitleContainingIgnoreCase(String username, String title);
}

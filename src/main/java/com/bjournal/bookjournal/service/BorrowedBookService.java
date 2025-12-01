package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.BorrowedBook;

import java.util.List;

public interface BorrowedBookService {
    List<BorrowedBook> findAllByUser(String username);
    void add(String text, String username);
    BorrowedBook update(Long id, String text);
    void delete(Long id);
}

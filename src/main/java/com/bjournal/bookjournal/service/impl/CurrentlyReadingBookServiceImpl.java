package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.CurrentlyReadingBook;
import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.model.exceptions.CurrentlyReadingBookNotFoundException;
import com.bjournal.bookjournal.repository.CurrentlyReadingBookRepository;
import com.bjournal.bookjournal.service.BookService;
import com.bjournal.bookjournal.service.CurrentlyReadingBookService;
import com.bjournal.bookjournal.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CurrentlyReadingBookServiceImpl implements CurrentlyReadingBookService {
    private final CurrentlyReadingBookRepository currentlyReadingBookRepository;
    private final UserService userService;
    private final BookService bookService;

    public CurrentlyReadingBookServiceImpl(CurrentlyReadingBookRepository currentlyReadingBookRepository, UserService userService, BookService bookService) {
        this.currentlyReadingBookRepository = currentlyReadingBookRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    @Override
    public List<CurrentlyReadingBook> findAllByUsername(String username) {
        return this.currentlyReadingBookRepository.findAllByUserUsername(username);
    }

    @Override
    public List<CurrentlyReadingBook> findAllByUsernameAndBookTitleContainingIgnoreCase(String username, String title) {
        return this.currentlyReadingBookRepository.findAllByUserUsernameAndBookTitleContainingIgnoreCase(username, title);
    }

    @Override
    public void add(String username, Long bookId) {
        User user = this.userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        Book book = this.bookService.findById(bookId).orElseThrow(()->new BookNotFoundException(bookId));

        this.currentlyReadingBookRepository.save(new CurrentlyReadingBook(null, null, book, user));
    }

    @Transactional
    @Override
    public void delete(String username, Long bookId) {
        CurrentlyReadingBook currentlyReadingBook = this.currentlyReadingBookRepository.findByUserUsernameAndBookId(username, bookId).orElse(null);
        if (currentlyReadingBook == null) return;
        this.currentlyReadingBookRepository.delete(currentlyReadingBook);
    }

    @Override
    public Optional<CurrentlyReadingBook> findByUsernameAndBookId(String username, Long bookId) {
        return this.currentlyReadingBookRepository.findByUserUsernameAndBookId(username, bookId);
    }
}

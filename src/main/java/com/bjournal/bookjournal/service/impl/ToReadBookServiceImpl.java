package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.ToReadBook;
import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.repository.ToReadBookRepository;
import com.bjournal.bookjournal.service.BookService;
import com.bjournal.bookjournal.service.ToReadBookService;
import com.bjournal.bookjournal.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ToReadBookServiceImpl implements ToReadBookService {
    private final ToReadBookRepository toReadBookRepository;
    private final UserService userService;
    private final BookService bookService;

    public ToReadBookServiceImpl(ToReadBookRepository toReadBookRepository, UserService userService, BookService bookService) {
        this.toReadBookRepository = toReadBookRepository;
        this.userService = userService;
        this.bookService = bookService;
    }


    @Override
    public Optional<ToReadBook> findById(Long id) {
        return this.toReadBookRepository.findById(id);
    }

    @Transactional
    @Override
    public void toggleToRead(String username, Long bookId) {
        User user = this.userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        Book book = this.bookService.findById(bookId).orElseThrow(()->new BookNotFoundException(bookId));
        if (this.toReadBookRepository.existsToReadBookByUserAndBook(user,book)) this.toReadBookRepository.deleteToReadBookByUserAndBook(user,book);
        else this.toReadBookRepository.save(new ToReadBook(book, user));
    }

    @Override
    public List<ToReadBook> findAllByUsername(String username) {
        return this.toReadBookRepository.findAllByUserUsername(username);
    }

    public Optional<ToReadBook> findByUsernameAndBookId(String username, Long bookId) {
        return this.toReadBookRepository.findByUserUsernameAndBookId(username, bookId);
    }

    @Override
    public List<ToReadBook> findAllByUserAndBookTitleContainingIgnoreCase(String username, String title) {
        return this.toReadBookRepository.findAllByUserUsernameAndBookTitleContainingIgnoreCase(username, title);
    }
}

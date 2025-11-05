package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.ToReadBook;
import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.repository.BookRepository;
import com.bjournal.bookjournal.repository.ToReadBookRepository;
import com.bjournal.bookjournal.repository.UserRepository;
import com.bjournal.bookjournal.service.ToReadBookService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ToReadBookServiceImpl implements ToReadBookService {
    private final ToReadBookRepository toReadBookRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public ToReadBookServiceImpl(ToReadBookRepository toReadBookRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.toReadBookRepository = toReadBookRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public Optional<ToReadBook> findById(Long id) {
        return this.toReadBookRepository.findById(id);
    }

    @Transactional
    @Override
    public void toggleToRead(String username, Long bookId) {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        Book book = this.bookRepository.findById(bookId).orElseThrow(()->new BookNotFoundException(bookId));
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

package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.UserReadBook;
import com.bjournal.bookjournal.repository.UserReadBookRepository;
import com.bjournal.bookjournal.service.UserReadBookService;
import com.bjournal.bookjournal.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserReadBookServiceImpl implements UserReadBookService {
    private final UserReadBookRepository userReadBookRepository;
    private final UserService userService;

    public UserReadBookServiceImpl(UserReadBookRepository userReadBookRepository, UserService userService) {
        this.userReadBookRepository = userReadBookRepository;
        this.userService = userService;
    }

    @Override
    public List<UserReadBook> findAllByUser(String username) {
        User user = this.userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return this.userReadBookRepository.findByUser(user);
    }

    @Override
    public Optional<UserReadBook> findByUserAndBook(String username, Book book) {
        User user = this.userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return this.userReadBookRepository.findByUserAndBook(user, book);
    }

    @Override
    public void add(LocalDate addedDate, String username, Book book) {
        if (addedDate == null || username == null || username.isBlank() || book == null) {
            throw new IllegalArgumentException();
        }
        User user = this.userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        this.userReadBookRepository.save(new UserReadBook(addedDate, user, book));
    }

    @Override
    @Transactional
    public void deleteByUserAndBook(String username, Book book) {
        User user = this.userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        this.userReadBookRepository.deleteByUserAndBook(user, book);
    }

    @Override
    public void update(UserReadBook userReadBook, LocalDate startedDate, LocalDate finishedDate) {
        if (startedDate.isAfter(finishedDate)) {
            throw new IllegalArgumentException("Started date cannot be after finished date");
        }
        userReadBook.setStartedDate(startedDate);
        userReadBook.setFinishedDate(finishedDate);
        this.userReadBookRepository.save(userReadBook);
    }

}

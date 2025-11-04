package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.ToReadBook;
import com.bjournal.bookjournal.model.User;
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

    public ToReadBookServiceImpl(ToReadBookRepository toReadBookRepository, UserRepository userRepository) {
        this.toReadBookRepository = toReadBookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<ToReadBook> findById(Long id) {
        return this.toReadBookRepository.findById(id);
    }

    @Transactional
    @Override
    public void toggleToRead(String username, Book book) {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        if (this.toReadBookRepository.existsToReadBookByUserAndBook(user,book)) this.toReadBookRepository.deleteToReadBookByUserAndBook(user,book);
        else this.toReadBookRepository.save(new ToReadBook(book, user));
    }

    @Override
    public List<ToReadBook> findAllByUser(String username) {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return this.toReadBookRepository.findAllByUser(user);
    }

    @Override
    public Optional<ToReadBook> findByUserAndBook(String username, Book book) {
        User user  = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return this.toReadBookRepository.findByUserAndBook(user, book);
    }
}

package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.UserReadBook;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.model.exceptions.UserReadBookAlreadyExistsException;
import com.bjournal.bookjournal.model.exceptions.UserReadBookNotFoundException;
import com.bjournal.bookjournal.repository.UserReadBookRepository;
import com.bjournal.bookjournal.service.BookService;
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
    private final BookService bookService;

    public UserReadBookServiceImpl(UserReadBookRepository userReadBookRepository, UserService userService, BookService bookService) {
        this.userReadBookRepository = userReadBookRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    @Override
    public List<UserReadBook> findAllByUsername(String username) {
        return this.userReadBookRepository.findAllByUserUsername(username);
    }

    @Override
    public Optional<UserReadBook> findLastByUserUsernameAndBookId(String username, Long bookId) {
        List<UserReadBook> books = this.userReadBookRepository.findAllByUserUsernameAndBookId(username, bookId);
        if (books.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(books.get(books.size() - 1));
    }

    @Override
    public void add(LocalDate addedDate, String username, Long bookId) {
        if (addedDate == null  || bookId == null) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        User user = this.userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Book book = this.bookService.findById(bookId).orElseThrow(()->new BookNotFoundException(bookId));
        this.userReadBookRepository.save(new UserReadBook(addedDate, user, book));
    }

    @Override
    @Transactional
    public void delete(String username, Long bookId) {
        UserReadBook userReadBook = findLastByUserUsernameAndBookId(username,bookId).orElse(null);
        if (userReadBook == null) return;
        this.userReadBookRepository.deleteAllByUserUsernameAndBookId(username,bookId);
    }

    @Override
    public void updateDates(String username, Long bookId, LocalDate startedDate, LocalDate finishedDate) {
        if (startedDate !=null && finishedDate!=null && startedDate.isAfter(finishedDate)) {
            throw new IllegalArgumentException("Started date cannot be after finished date");
        }
        UserReadBook userReadBook = findLastByUserUsernameAndBookId(username, bookId).orElseThrow(()->new UserReadBookNotFoundException(username,bookId));
        userReadBook.setStartedDate(startedDate);
        userReadBook.setFinishedDate(finishedDate);
        this.userReadBookRepository.save(userReadBook);
    }

    @Override
    public List<UserReadBook> findAllByUserAndBookTitleContainingIgnoreCase(String username, String title) {
        return this.userReadBookRepository.findAllByUserUsernameAndBookTitleContainingIgnoreCase(username, title);
    }

}
